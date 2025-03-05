package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        log.info("Received request to create recommendation: {}", recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(
            @Valid @ModelAttribute RequestFilterDto filter) {
        log.info("Received request to get recommendations with filter: {}", filter);
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(
            @PathVariable @Positive Long id) {
        log.info("Received request to get recommendation with id: {}", id);
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(
            @PathVariable @Positive Long id,
            @Valid @RequestBody RejectionDto rejection) {
        log.info("Received request to reject recommendation with id: {}", id);
        return recommendationRequestService.rejectRequest(id, rejection);
    }

}
