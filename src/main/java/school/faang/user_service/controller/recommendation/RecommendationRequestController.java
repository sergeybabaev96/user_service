package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;


import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    public ResponseEntity<RecommendationRequestDto> requestRecommendation(
            @Validated @RequestBody RecommendationRequestDto recommendationRequest) {

        RecommendationRequestDto createdRequest = recommendationRequestService.create(recommendationRequest);
        return ResponseEntity.ok(createdRequest);
    }

    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(
            @RequestBody RequestFilterDto filter) {
        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }

    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable long id) {
        RecommendationRequestDto recommendationRequest = recommendationRequestService.getRequest(id);
        return ResponseEntity.ok(recommendationRequest);
    }

    public ResponseEntity<RecommendationRequestDto> rejectRequest(
            @PathVariable long id,
            @Validated @RequestBody RejectionDto rejection) {

        RecommendationRequestDto rejectRequest = recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok(rejectRequest);
    }
}
