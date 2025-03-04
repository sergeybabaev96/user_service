package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferDto;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final int RECOMMENDATION_MIN_DISTANCE_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
        var lastAuthorRecommendation = recommendationRepository.findLastRecommendationByAuthorId(
                recommendation.getAuthorId());

        if (lastAuthorRecommendation.isPresent()
                && LocalDateTime.now()
                .minusMonths(RECOMMENDATION_MIN_DISTANCE_MONTHS)
                .isAfter(lastAuthorRecommendation.get().getUpdatedAt())) {
            throw new DataValidationException(String.format(
                    "С момента прошлой рекомендации прошло меньше %s месяцев",
                    RECOMMENDATION_MIN_DISTANCE_MONTHS));
        }

        var existedSkillsInRecommendation = recommendation.getSkillOffers()
                .stream()
                .filter(dto -> skillRepository.findById(dto.getSkillId()).isPresent())
                .toList();
        if (existedSkillsInRecommendation.size() != recommendation.getSkillOffers().stream().distinct().count()) {
            throw new DataValidationException(String.format(
                    "Навыки %s не зарегистрированы в системе",
                    String.join(
                            ", ",
                            recommendation.getSkillOffers()
                                    .stream()
                                    .map(SkillOfferDto::getSkillId)
                                    .filter(skillId -> existedSkillsInRecommendation.stream()
                                            .filter(existedSkill -> existedSkill.getSkillId() == skillId)
                                            .findFirst()
                                            .isEmpty())
                                    .map(skillRepository::findById)
                                    .filter(Optional::isPresent)
                                    .map(x -> x.get().getTitle())
                                    .toList())));
        }

        var receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());

        recommendation.getSkillOffers()
                .forEach(dto ->
                {
                    var existedSkill = receiverSkills.stream()
                            .filter(x -> x.getId() == dto.getSkillId())
                            .findFirst();
                    if (existedSkill.isPresent()
                            && userSkillGuaranteeRepository.findByGuarantorId(recommendation.getAuthorId()).isEmpty()) {
                        userSkillGuaranteeRepository.create(
                                recommendation.getReceiverId(),
                                dto.getSkillId(),
                                recommendation.getAuthorId());
                    }

                    skillOfferRepository.create(dto.getSkillId(), recommendation.getId());
                });

        recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());

        return recommendation;
    }
}
