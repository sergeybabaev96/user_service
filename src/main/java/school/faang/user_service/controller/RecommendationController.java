package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        log.info("The process of transmitting the recommendation to the user begins");
        RecommendationDto returnedRecommendation = recommendationService.create(recommendationDto);
        log.info("The recommendation has been passed");
        return returnedRecommendation;
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        log.info("Starting update of recommendation");
        RecommendationDto updatedRecommendationDto = recommendationService.update(recommendationDto);
        log.info("The update is finished");
        return updatedRecommendationDto;
    }

    public void deleteRecommendation(Long id) {
        log.info("Starting to delete the recommendation");
        recommendationService.delete(id);
        log.info("The recommendation has been removed");
    }

    public List<RecommendationDto> getAllGivenRecommendation(Long authorId) {
        outputOfRecommendations(recommendationService.getAllGivenRecommendation(authorId));
        return recommendationService.getAllGivenRecommendation(authorId);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        outputOfRecommendations(recommendationService.getAllUserRecommendations(receiverId));
        return recommendationService.getAllGivenRecommendation(receiverId);
    }

    private void outputOfRecommendations(List<RecommendationDto> recommendationDtos) {
        recommendationDtos.forEach(r ->
                log.info("Author's id: {}\nReceiver's id{}\nContent:{}\nSkillOffers:{}\nCreated at: {}",
                        r.getAuthorId(), r.getReceiverId(), r.getContent(), r.getSkillOffersDto(), r.getCreatedAt())
        );
    }
}
