package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.GetAllRecommendationsResponse;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationResponse;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public CreateRecommendationResponse giveRecommendation(@RequestBody CreateRecommendationRequest
                                                                       recommendationRequest) {
        return recommendationService.create(recommendationRequest);
    }

    public UpdateRecommendationResponse updateRecommendation(UpdateRecommendationRequest recommendationRequest) {
        return recommendationService.update(recommendationRequest);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<GetAllRecommendationsResponse> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<GetAllRecommendationsResponse> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
