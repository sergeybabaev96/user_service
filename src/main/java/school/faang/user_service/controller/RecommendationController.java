package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(@Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(@Valid RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    public boolean deleteRecommendation(@PathVariable @Min(1) long id) {
        return recommendationService.delete(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(
            @PathVariable @Min(1) long recieverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return recommendationService.getAllUserRecommendations(recieverId,page,size);
    }
}