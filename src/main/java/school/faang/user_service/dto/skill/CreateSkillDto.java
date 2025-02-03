package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateSkillDto(Long id, @NotBlank(message = "Field cannot be blank") String title) {
}
