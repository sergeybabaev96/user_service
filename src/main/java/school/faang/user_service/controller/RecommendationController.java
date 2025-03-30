package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommendations")
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationDto giveRecommendation(@Valid @RequestBody RecommendationDto recommendation) {
        if (isRecommendationValidation(recommendation)) {
            return recommendationService.create(recommendation);
        } else {
            throw new DataValidationException("Рекомендация не прошла проверку");
        }
    }

    @PutMapping("/recommendations/update")
    public RecommendationDto updateRecommendation(@Valid @RequestBody RecommendationDto updated) {
        if (isRecommendationValidation(updated)) {
            return recommendationService.update(updated);
        } else {
            throw new DataValidationException("Рекомендация не прошла проверку");
        }
    }

    private boolean isRecommendationValidation(RecommendationDto recommendation) {
        return recommendation != null && !recommendation.getContent().isBlank();
    }

    @DeleteMapping("/recommendations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{id}/recommendations")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/author/{id}/recommendations")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
