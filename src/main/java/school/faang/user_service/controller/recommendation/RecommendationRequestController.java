package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto != null && !recommendationRequestDto.getMessage().isEmpty()) {
            return recommendationRequestService.create(recommendationRequestDto);
        }

        return null;
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        if (filter != null) {
            return recommendationRequestService.getRequests(filter);
        }

        return null;
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
