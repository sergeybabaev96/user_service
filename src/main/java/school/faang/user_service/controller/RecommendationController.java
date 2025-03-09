package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommendation/create")
    public RecommendationDto giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping("/recommendation/update")
    public RecommendationDto updateRecommendation(@RequestBody @Valid RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    @DeleteMapping("/recommendation/{recommendationId}/delete")
    public boolean deleteRecommendation(@PathVariable @Min(1) long recommendationId) {
        return recommendationService.delete(recommendationId);
    }

    @GetMapping("/recommendation/{receiverId}/getAllUserRecommendations")
    public Page<RecommendationDto> getAllUserRecommendations(
            @PathVariable @Min(1) long receiverId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(5) int size
    ) {
        return recommendationService.getAllUserRecommendations(receiverId, page, size);
    }

    @GetMapping("/recommendation/{authorId}/getAllGivenRecommendations")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @PathVariable @Min(1) long authorId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(5) int size) {

        return recommendationService.getAllGivenRecommendations(authorId, page, size);
    }
}