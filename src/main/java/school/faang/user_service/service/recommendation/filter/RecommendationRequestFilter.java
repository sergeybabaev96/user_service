package school.faang.user_service.service.recommendation.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto requestFilterDto);

    List<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                      RequestFilterDto requestFilterDto);
}
