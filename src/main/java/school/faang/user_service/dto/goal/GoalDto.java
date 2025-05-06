package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
    private Long parentId;

    @NotBlank(message = "Email is mandatory")
    @Size(max = 64, message = "Max length tittle — 64 char")
    private String title;
    @Size(max = 128, message = "Max length description — 128 char")
    private String description;
}
