package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.Objects;

@Component
public class RequesterFilter implements Filter<RecommendationRequest, RecommendationRequestDto> {

    @Override
    public boolean isApplicable(RecommendationRequestDto dto) {
        return dto.getRequesterId() != 0;
    }

    @Override
    public List<RecommendationRequest> apply(List<RecommendationRequest> requests, RecommendationRequestDto filters) {
        return requests.stream()
                .filter(r -> Objects.equals(r.getRequester().getId(), filters.getRequesterId()))
                .toList();
    }
}
