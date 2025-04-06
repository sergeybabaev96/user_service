package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public void deletePastEvents(int batchSize) {
        List<Event> allEvents = eventRepository.findAll();
        List<Long> idsToDelete = allEvents.stream()
                .filter(event -> event.getEndDate() != null && event.getEndDate().isBefore(LocalDateTime.now()))
                .map(Event::getId)
                .toList();

        if (idsToDelete.isEmpty()) {
            log.info("Нет завершённых событий для удаления");
            return;
        }

        List<List<Long>> partitions = partitionList(idsToDelete, batchSize);
        List<Thread> threads = new ArrayList<>();

        for (List<Long> batch : partitions) {
            Thread thread = new Thread(() -> {
                try {
                    eventRepository.deleteAllByIdInBatch(batch);
                    log.info("Удалена пачка событий: {}", batch);
                } catch (Exception e) {
                    log.error("Ошибка при удалении событий: {}", batch, e);
                }
            });
            thread.start();
            threads.add(thread);
        }

        // Дождаться завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.warn("Удаляющий поток был прерван", e);
                Thread.currentThread().interrupt();
            }
        }

        log.info("Удаление завершённых событий завершено. Удалено: {}", idsToDelete.size());
    }

    private List<List<Long>> partitionList(List<Long> ids, int size) {
        List<List<Long>> partitions = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += size) {
            partitions.add(ids.subList(i, Math.min(i + size, ids.size())));
        }
        return partitions;
    }
}
