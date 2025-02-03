package school.faang.user_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MentorshipRequestFilterDto(
        @Size(max = 255, message = "descriptionPattern length must not exceed 255 characters")
        String descriptionPattern,
        @Size(max = 255, message = "authorPattern length must not exceed 255 characters")
        String authorPattern,
        @Size(max = 255, message = "receiverPattern length must not exceed 255 characters")
        String receiverPattern,
        @Size(max = 255, message = "statusPattern length must not exceed 255 characters")
        String statusPattern
) {
}
