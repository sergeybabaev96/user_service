package school.faang.user_service.utils;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;

public class ValidationUtils {
    public static void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent().isBlank()) {
            throw new DataValidationException("The content of the recommendation should not be empty.");
        }
    }

    public static void validateRecommendationDate(RecommendationDto recommendation) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(currentDate, recommendation.getCreatedAt().toLocalDate());

        if (period.toTotalMonths() <= 6) {
            throw new DateTimeException("It is not possible to give a recommendation to this user, since the last " +
                    " recommendation was made less than 6 months ago.");
        }
    }

    public static void validateSkills(RecommendationDto recommendation, SkillOfferRepository skillOfferRepository) {
        List<Long> skillOffersList = skillOfferRepository.findAllSkillIds();
        List<Long> skillOffersIds = recommendation.getSkillOfferIds();

        if (!new HashSet<>(skillOffersList).containsAll(skillOffersIds)) {
            throw new DataValidationException("All recommendation skills must be in the system.");
        }
    }
}
