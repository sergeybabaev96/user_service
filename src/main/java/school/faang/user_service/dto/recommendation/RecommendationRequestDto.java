package school.faang.user_service.dto.recommendation;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private long id;
    private String message;
    private List<Long> skillsId;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
