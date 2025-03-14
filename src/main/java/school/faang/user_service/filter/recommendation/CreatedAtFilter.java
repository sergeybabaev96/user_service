package school.faang.user_service.filter.recommendation;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;

@Component
public class CreatedAtFilter extends DateTimeFilterBase {

    @Nullable
    @Override
    protected LocalDateTime getTimeStamp(RecommendationRequest recommendationRequest) {
        return recommendationRequest.getCreatedAt();
    }

    @Nullable
    @Override
    protected LocalDateTime getToTimeStamp(RequestFilterDto filterDto) {
        return filterDto.getCreatedAtTo();
    }

    @Nullable
    @Override
    protected LocalDateTime getFromTimeStamp(RequestFilterDto filterDto) {
        return filterDto.getCreatedAtFrom();
    }
}
