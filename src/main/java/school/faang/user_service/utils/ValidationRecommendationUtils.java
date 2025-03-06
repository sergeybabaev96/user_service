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
    private static final String CONTENT_NULL_EXCEPTION = "Содержание рекомендации не может быть null";
    private static final String CONTENT_EMPTY_EXCEPTION = "Содержание рекомендации не может быть пустым";
    private static final String DATE_EXCEPTION = "Невозможно дать рекомендацию этому пользователю, поскольку " +
            "последняя рекомендация была сделана менее 6 месяцев назад.";
    private static final String DATE_NULL_EXCEPTION = "Дата создания не может быть null";
    private static final String SKILL_OFFER_VALID_EXCEPTION = "Предложение о приобретении навыка с идентификатором: " +
            "%d недействительно.";

    public static void validateRecommendationContent(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null) {
            throw new DataValidationException(CONTENT_NULL_EXCEPTION);
        } else if (recommendationDto.getContent().isBlank()) {
            throw new DataValidationException(CONTENT_EMPTY_EXCEPTION);
        }
    }

    public static void validateRecommendationDate(RecommendationDto recommendationDto) {
        if (recommendationDto.getCreatedAt() == null) {
            throw new DataValidationException(DATE_NULL_EXCEPTION);
        }
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(currentDate, recommendationDto.getCreatedAt().toLocalDate());

        if (period.toTotalMonths() <= 6) {
            throw new DateTimeException(DATE_EXCEPTION);
        }
    }

    public static void validateSkills(RecommendationDto recommendationDto, List<SkillOffer> allSkillOffers) {
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
