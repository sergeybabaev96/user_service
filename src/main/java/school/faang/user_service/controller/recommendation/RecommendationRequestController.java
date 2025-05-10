package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filters.Filter;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validators.Validator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService requestService;
    //todo: Validator сделать возможность сортировки, задать порядок валидирования.
    private final List<Validator<RecommendationRequestDto>> validators;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        log.info("validatorsLevelFirst.size: {}", validators.size());
        validators.forEach(validator -> validator.validate(recommendationRequest));
        return requestService.create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return requestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(Long id) {
        return requestService.getRequest(id);
    }
}
