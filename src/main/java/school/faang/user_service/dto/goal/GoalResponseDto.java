package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDto {
    private long id;
    private Long parentId;
    private String title;
    private String description;
    private boolean completed;
    private String deadline;
    private String createdDate;
    private String updatedDate;
    private Long mentorId;
    private List<Long> invitationsIds;
    private List<Long> usersIds;
    private List<Long> skillsToAchieveIds;
}
