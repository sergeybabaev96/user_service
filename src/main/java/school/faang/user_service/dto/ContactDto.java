package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import school.faang.user_service.entity.contact.ContactType;

@Builder
public record ContactDto(
        Long id,
        Long userId,
        @NotBlank(message = "Contact cannot be blank")
        String contact,
        ContactType type
) {
}
