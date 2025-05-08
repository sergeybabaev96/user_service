package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalRequestDto {
    private Long parentId;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 64, message = "Max length tittle — 64 char")
    private String title;

    @Size(max = 128, message = "Max length description — 128 char")
    private String description;

    private boolean completed;

    private List<Long> skillIds;
}
