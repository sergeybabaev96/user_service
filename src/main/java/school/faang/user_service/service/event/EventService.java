package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    @Qualifier("taskService")
    public final Executor eventDeletionExecutor;
    private final EventRepository eventRepository;

    public void deletePastEvents(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize должен быть с положительным числом");
        }
        List<Long> idsToDelete = eventRepository.findIdsByEndDateBefore(LocalDateTime.now());

        if (idsToDelete.isEmpty()) {
            log.warn("Нет завершённых событий для удаления");
            return;
        }

        List<List<Long>> partitions = ListUtils.partition(idsToDelete, batchSize);

        List<CompletableFuture<Void>> futures = partitions.stream()
                .map(batch -> CompletableFuture.runAsync(() -> deleteBatch(batch), eventDeletionExecutor))
                .toList();

        futures.forEach(f -> {
            try {
                f.join();
            } catch (Exception e) {
                log.error("Ошибка при ожидании завершения задачи", e);
            }
        });

        log.info("Удаление завершённых событий завершено. Удалено: {}", idsToDelete.size());
    }

    @Transactional
    public void deleteBatch(List<Long> batch) {
        try {
            eventRepository.deleteByIds(batch);
            log.info("Удалена пачка событий: {}", batch);
        } catch (Exception e) {
            log.error("Ошибка при удалении событий: {}", batch, e);
        }
    }
}
