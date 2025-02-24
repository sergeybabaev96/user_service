package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

@Builder
public record CreateRecommendationRequestResponse(long id,
                                                  String message,
                                                  RequestStatus status,
                                                  List<String> skills,
                                                  long requesterId,
                                                  long receiverId) {
}
