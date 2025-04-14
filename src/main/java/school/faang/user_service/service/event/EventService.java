package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ExecutorService executor = Executors.newFixedThreadPool(5); // или настраиваемый бин

    public void deletePastEvents(int batchSize) {
        List<Long> idsToDelete = eventRepository.findIdsByEndDateBefore(LocalDateTime.now());

        if (idsToDelete.isEmpty()) {
            log.warn("Нет завершённых событий для удаления");
            return;
        }

        List<List<Long>> partitions = ListUtils.partition(idsToDelete, batchSize);

        List<CompletableFuture<Void>> futures = partitions.stream()
                .map(batch -> CompletableFuture.runAsync(() -> deleteBatch(batch), executor))
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
