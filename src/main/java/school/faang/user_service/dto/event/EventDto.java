package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.utility.validator.event.dto.ValidEventDates;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ValidEventDates
public record EventDto(

        Long id,
        @NotBlank
        @Size(max = 64)
        String title,
        @NotBlank
        @Size(max = 4096)
        String description,
        @NotNull
        @Future
        LocalDateTime startDate,
        @NotNull
        @Future
        LocalDateTime endDate,
        @NotBlank
        @Size(max = 128)
        String location,
        @Positive
        int maxAttendees,
        @NotNull
        Long ownerId,
        @NotEmpty
        List<Long> relatedSkillIds,
        @NotNull
        EventType type,
        @NotNull
        EventStatus status) {
}