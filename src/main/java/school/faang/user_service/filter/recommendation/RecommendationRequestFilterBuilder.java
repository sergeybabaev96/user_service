package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.filter.recommendation.filters.CreatedAtFilter;
import school.faang.user_service.filter.recommendation.filters.ReceiverIdFilter;
import school.faang.user_service.filter.recommendation.filters.RequesterIdFilter;
import school.faang.user_service.filter.recommendation.filters.SkillIdFilter;
import school.faang.user_service.filter.recommendation.filters.StatusFilter;

import java.util.ArrayList;
import java.util.List;

public class RecommendationRequestFilterBuilder {

    public static List<RecommendationRequestFilterStrategy> buildStrategies(RequestFilterDto filter) {
        List<RecommendationRequestFilterStrategy> strategies = new ArrayList<>();

        if (filter.getRequesterId() != null) {
            strategies.add(new RequesterIdFilter(filter.getRequesterId()));
        }
        if (filter.getReceiverId() != null) {
            strategies.add(new ReceiverIdFilter(filter.getReceiverId()));
        }
        if (filter.getStatus() != null) {
            strategies.add(new StatusFilter(filter.getStatus()));
        }
        if (filter.getSkillId() != null) {
            strategies.add(new SkillIdFilter(filter.getSkillId()));
        }
        if (filter.getCreatedAt() != null) {
            strategies.add(new CreatedAtFilter(filter.getCreatedAt()));
        }

        return strategies;
    }
}
