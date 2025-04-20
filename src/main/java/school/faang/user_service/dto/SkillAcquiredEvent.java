package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillAcquiredEvent {
    private long userId;
    private long skillId;
}
