package school.faang.user_service.filter.Event;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class DateRangeFilter implements EventFilter{
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    @Override
    public boolean matches(Event event) {
        return (startDate == null || !event.getStartDate().isBefore(startDate)) &&
                (endDate == null || !event.getEndDate().isAfter(endDate));
    }
}
