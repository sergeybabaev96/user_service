package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@AllArgsConstructor
public class RecommendationController {
    @Autowired
    private  final RecommendationService recommendationService;

    @Autowired
    private final RecommendationMapper recommendationMapper;

    private static final String NEGATIVE_ID = "Id is negative";

    @PostMapping("/giverecommendation")
    @ResponseBody
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        if (recommendationDtoIsValid(recommendationDto)) {
            Recommendation recommendation = recommendationService.create(
                    recommendationMapper.toEntity(recommendationDto));
            return recommendationMapper.toDto(recommendation);
        } else {
            throw new DataValidationException("Content is empty");
        }
    }

    @PostMapping("/updaterecommendation")
    @ResponseBody
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto recommendationDto) {
        if (recommendationDtoIsValid(recommendationDto)) {
            Recommendation recommendation = recommendationService.update(
                    recommendationMapper.toEntity(recommendationDto));
            return recommendationMapper.toDto(recommendation);
        } else {
            throw new DataValidationException("Content is empty");
        }
    }

    @PostMapping("/deleterecommendation")
    @ResponseBody
    public void deleteRecommendation(@RequestParam long id) {
        if (idIsValid(id)) {
            recommendationService.delete(id);
        } else {
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    @GetMapping("/getalluserrecommendations")
    @ResponseBody
    public List<RecommendationDto> getAllUserRecommendations(@RequestParam long receiverId) {
        if (idIsValid(receiverId)) {
            return recommendationService.getAllUserRecommendations(receiverId);
        } else {
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    @GetMapping("/getallgivenrecommendations")
    @ResponseBody
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        if (idIsValid(authorId)) {
            return recommendationService.getAllGivenRecommendations(authorId);
        } else {
            throw new DataValidationException(NEGATIVE_ID);
        }
    }

    private boolean recommendationDtoIsValid(RecommendationDto recommendationDto) {
        return !recommendationDto.getContent().isEmpty();
    }

    private boolean idIsValid(Long id) {
        return id >= 0;
    }
}
