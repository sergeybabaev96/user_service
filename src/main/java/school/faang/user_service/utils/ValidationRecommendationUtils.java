package school.faang.user_service.utils;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ValidationRecommendationUtils {
    public static void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null) {
            throw new DataValidationException("The content of the recommendation can't be null");
        } else if (recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("The content of the recommendation can't be null");
        }
    }

    public static void validateRecommendationDate(RecommendationDto recommendationDto) {
        if (recommendationDto.getCreatedAt() == null) {
            throw new DataValidationException("Data of creating can't be null");
        }
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(currentDate, recommendationDto.getCreatedAt().toLocalDate());

        if (period.toTotalMonths() <= 6) {
            throw new DateTimeException("It is not possible to give a recommendation to this user, since the last" +
                    " recommendation was made less than 6 months ago.");
        }
    }

    public static void validateSkills(RecommendationDto recommendationDto, List<SkillOffer> allSkillOffers) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffersDto();
        List<Long> allSkillOffersIds = allSkillOffers.stream()
                .map(SkillOffer::getId)
                .toList();

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            if (!allSkillOffersIds.contains(skillOfferDto.getId())) {
                throw new DataValidationException(String.format("Skill offer with id: %d is not valid.",
                        skillOfferDto.getId()));
            }
        }
    }
}
