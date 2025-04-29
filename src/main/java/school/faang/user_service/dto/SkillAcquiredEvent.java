package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillAcquiredEvent implements EventMessage {

    @NotNull
    @Positive
    private Long authorId;

    @NotNull
    @Positive
    private Long recipientId;

    @NotNull
    @Positive
    private Long skillId;
}
