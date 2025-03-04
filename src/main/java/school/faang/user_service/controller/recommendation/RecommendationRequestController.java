package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest){
//Нужно провалидировать запрос на рекомендацию и проверить, что запрос содержит непустое сообщение.
//
//После валидации нужно вызвать метод create(recommendationRequest) класса RecommendationRequestService.
        recommendationRequestService.create(recommendationRequest);
        return recommendationRequest;
    }
}
