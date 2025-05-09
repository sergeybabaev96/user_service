package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    public Optional<RecommendationRequestDto> requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto != null && !recommendationRequestDto.getMessage().isEmpty()) {
            return recommendationRequestService.create(recommendationRequestDto);
        }

        return Optional.empty();
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        if (filter != null) {
            return recommendationRequestService.getRequests(filter);
        }

        return new ArrayList<>();
    }

    public Optional<RecommendationRequestDto> getRecommendationRequest(long id) {
        return Optional.ofNullable(recommendationRequestService.getRequest(id));
    }

    public Optional<RecommendationRequestDto> rejectRequest(long id, RejectionDto rejection) {
        return Optional.ofNullable(recommendationRequestService.rejectRequest(id, rejection));
    }
}
