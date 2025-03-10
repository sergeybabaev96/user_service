package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RejectionReasonFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getRejectionReason() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> source, RequestFilterDto filterDto) {
        return source.filter(x -> x.getRejectionReason().equals(filterDto.getRejectionReason()));
    }
}
