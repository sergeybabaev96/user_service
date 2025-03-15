package school.faang.user_service.filter.recommendation;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
public class MessageFilter extends StringPatternFilterBase {

    @Override
    protected String getFieldToCheck(RecommendationRequest recommendationRequest) {
        return recommendationRequest.getMessage();
    }

    @Nullable
    @Override
    protected String getPattern(RequestFilterDto filterDto) {
        return filterDto.getMessagePattern();
    }
}
