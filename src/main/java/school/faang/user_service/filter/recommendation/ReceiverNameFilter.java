package school.faang.user_service.filter.recommendation;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
public class ReceiverNameFilter extends StringPatternFilterBase {

    @Override
    protected String getFieldToCheck(RecommendationRequest recommendationRequest) {
        return recommendationRequest.getReceiver().getUsername();
    }

    @Nullable
    @Override
    protected String getPattern(RequestFilterDto filterDto) {
        return filterDto.getReceiverNamePattern();
    }
}
