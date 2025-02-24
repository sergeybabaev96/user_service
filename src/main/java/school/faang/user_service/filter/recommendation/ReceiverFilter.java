package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.Objects;

@Component
public class ReceiverFilter implements Filter<RecommendationRequest, RecommendationRequestFilterDto> {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto dto) {
        return dto.receiverId() != null;
    }

    @Override
    public List<RecommendationRequest> apply(List<RecommendationRequest> requests, RecommendationRequestFilterDto filters) {
        return requests.stream()
                .filter(r -> Objects.equals(r.getReceiver().getId(), filters.receiverId()))
                .toList();
    }
}
