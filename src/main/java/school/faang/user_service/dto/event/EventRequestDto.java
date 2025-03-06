package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventRequestDto(
        @NotBlank
        String title,
        @NotNull
        LocalDateTime startDate,
        LocalDateTime endDate,
        @NotNull
        Long ownerId,
        @NotBlank
        String description,
        List<Long> relatedSkillsIds,
        String location,
        int maxAttendees,
        @NotNull
        EventType eventType,
        @NotNull
        EventStatus eventStatus) {
}