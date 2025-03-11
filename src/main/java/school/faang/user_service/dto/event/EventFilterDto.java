package school.faang.user_service.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventFilterDto(

        String titleContains,

        LocalDateTime startDateLaterThan,

        Integer maxAttendeesLessThan) {
}
