package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationValidation {

    private static final Duration MIN_RECOMMENDATION_INTERVAL = Duration.ofDays(181);
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public static boolean textAvailability(RecommendationDto recommendationDto) {
        if (!recommendationDto.getContent().isEmpty()) {
            return true;
        }
        throw new DataValidationException("В рецензии должен содержатся текст");
    }

    public void checkRecommendationInterval(RecommendationDto recommendation) {
        Optional<Recommendation> lastRecommendation = recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(),
                        recommendation.getReceiverId());
        if (lastRecommendation.isPresent()) {
            Duration interval = Duration.between(lastRecommendation.get().getCreatedAt(),
                    recommendation.getCreatedAt());
            if (interval.compareTo(MIN_RECOMMENDATION_INTERVAL) < 0) {
                throw new DataValidationException("Минимальный интервал между рекомендациями составляет "
                        + MIN_RECOMMENDATION_INTERVAL.toDays() + " дней.");
            }
        }
    }

    public boolean checkingSkills(RecommendationDto recommendationDto) {
        List<Long> recommendedSkills = recommendationDto.getSkillOffers().stream()
                .map(SkillOfferDto::getId)
                .toList();
        List<Long> existingSkills = skillRepository.findAll().stream()
                .map(Skill::getId)
                .toList();
        if (existingSkills.containsAll(recommendedSkills)) {
            return true;
        } else {
            throw new DataValidationException("Skill не найден в базе данных");
        }
    }
}
