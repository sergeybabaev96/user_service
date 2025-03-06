package school.faang.user_service.dto.skill;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SkillDto {
    private long id;
    private String title;
    private List<Long> userIds;
    private List<Long> goalIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
