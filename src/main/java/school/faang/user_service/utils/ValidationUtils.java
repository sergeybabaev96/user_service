package school.faang.user_service.utils;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;

public class ValidationUtils {
    public static void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("The content of the recommendationDto should not be empty.");
        }
    }

    public static void validateRecommendationDate(Recommendation recommendation) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(currentDate, recommendation.getCreatedAt().toLocalDate());

        if (period.toTotalMonths() <= 6) {
            throw new DateTimeException("It is not possible to give a recommendationDto to this user, since the last " +
                    " recommendationDto was made less than 6 months ago.");
        }
    }

    public static void validateSkills(Recommendation recommendation, SkillOfferRepository skillOfferRepository) {
        List<SkillOffer> skillOffersList = skillOfferRepository.findAllSkillOffers();
        List<SkillOffer> skillOffersIds = recommendation.getSkillOffers();

        if (!new HashSet<>(skillOffersList).containsAll(skillOffersIds)) {
            throw new DataValidationException("All recommendationDto skills must be in the system.");
        }
    }
}
