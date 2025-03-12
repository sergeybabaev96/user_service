package school.faang.user_service.utils.validationUtils;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendationValidation {
    private static final String CONTENT_NULL_EXCEPTION = "The content of the recommendation cannot be null";
    private static final String CONTENT_EMPTY_EXCEPTION = "The content of the recommendation cannot be empty";
    private static final String DATE_EXCEPTION = "It is not possible to give a recommendation to this user because the"+
            " last recommendation was offered less than 6 months ago.";
    private static final String DATE_NULL_EXCEPTION = "The date of the recommendation cannot be null";
    private static final String RECOMMENDATION_NULL_EXCEPTION = "The recommendation cannot be null";
    private static final String SKILL_OFFER_VALID_EXCEPTION = "An offer to purchase a skill with the ID: " +
            "%d is invalid.";
    private static final int MOUNT_COUNT_LIMIT = 6;

    public static void validateRecommendationContent(String content) {
        if (content == null) {
            throw new DataValidationException(CONTENT_NULL_EXCEPTION);
        } else if (content.isBlank()) {
            throw new DataValidationException(CONTENT_EMPTY_EXCEPTION);
        }
    }

    public static void validateRecommendationDate(LocalDateTime lastRecommendation) {
        if (lastRecommendation == null) {
            throw new DataValidationException(DATE_NULL_EXCEPTION);
        } else if (lastRecommendation.isAfter(LocalDateTime.now().minusMonths(MOUNT_COUNT_LIMIT))) {
            throw new DataValidationException(DATE_EXCEPTION);
        }
    }

    public static void validateSkills(RecommendationDto recommendationDto, List<SkillOffer> allSkillOffers) {
        if (recommendationDto == null) {
            throw new DataValidationException(RECOMMENDATION_NULL_EXCEPTION);
        }
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffersDto();
        List<Long> allSkillOffersIds = allSkillOffers.stream()
                .map(SkillOffer::getId)
                .toList();

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            if (!allSkillOffersIds.contains(skillOfferDto.getId())) {
                throw new DataValidationException(String.format(SKILL_OFFER_VALID_EXCEPTION,
                        skillOfferDto.getId()));
            }
        }
    }
}