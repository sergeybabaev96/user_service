package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.RecommendationCreateDtoValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationCreateDtoValidator recommendationCreateDtoValidator;
    private final RecommendationMapper recommendationMapper;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationCreateDto recommendationCreateDto) {
        recommendationCreateDtoValidator.validate(recommendationCreateDto);
            Recommendation recommendation = recommendationService
                .create(recommendationCreateDto.authorId(), recommendationCreateDto.receiverId(),
                        recommendationCreateDto.skillOffersId(), recommendationCreateDto.content(),
                        recommendationCreateDto.createdAt());
        return recommendationMapper.toDto(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationCreateDto updated) {
        recommendationCreateDtoValidator.validate(updated);
        Recommendation recommendation = recommendationService
                .update(updated.authorId(), updated.receiverId(), updated.skillOffersId(),
                        updated.content(), updated.createdAt());
        return recommendationMapper.toDto(recommendation);
    }

    @DeleteMapping("/{recommendationId}")
    public void deleteRecommendation(@PathVariable long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        List<Recommendation> allUserRecommendations = recommendationService.getAllUserRecommendations(receiverId);
        return recommendationMapper.toListRecommendationDtos(allUserRecommendations);
    }

    @GetMapping("/author/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        List<Recommendation> allGivenRecommendations = recommendationService.getAllGivenRecommendations(authorId);
        return recommendationMapper.toListRecommendationDtos(allGivenRecommendations);
    }
}
