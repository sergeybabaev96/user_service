package school.faang.user_service.filter.request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.stream.Stream;

@Component
public class RequesterIdFilter implements RecommendationRequestFilter {

    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getRequesterId() != null;
    }

    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationStream,
                                               RequestFilterDto requestFilterDto) {
        return recommendationStream.filter(request -> request
                .getRequester()
                .getId()
                .equals(requestFilterDto.getRequesterId()));
    }
}
