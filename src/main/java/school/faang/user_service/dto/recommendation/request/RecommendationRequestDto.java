package school.faang.user_service.dto.recommendation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private String status;
    private List<Long> skills;
    private Long requesterId;
    private Long receiverId;
    private String createdAt;
    private String updatedAt;
    private String rejectionReason;
}
