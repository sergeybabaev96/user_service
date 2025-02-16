package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    @Min(value = 1, message = "Id must be greater than 0")
    Long id;
    @NotBlank(message = "Title should not be blank")
    @Size(max = 255, message = "The length must not exceed 255 characters")
    String title;
}
