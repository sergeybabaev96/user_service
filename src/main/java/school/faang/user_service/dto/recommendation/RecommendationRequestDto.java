package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private long id;
    private String message;
    private RequestStatus status;
    private List<Long> skillsId;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;





    // id, message, status, список skills, requesterId, receiverId, createdAt, updatedAt.
}
