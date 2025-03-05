package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferDto;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final int RECOMMENDATION_MIN_DISTANCE_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(@NotNull RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        var receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
        recommendation.getSkillOffers()
                .forEach(dto -> createSkillOffer(recommendation, dto, receiverSkills));

        var recommendationId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());

        var createdRecommendation = recommendationRepository.findById(recommendationId);

        if (createdRecommendation.isEmpty()) {
            throw new DataRetrievalFailureException("Не удалось получить созданную рекомендацию");
        }

        return recommendationMapper.toDto(createdRecommendation.get());
    }

    private void validateRecommendation(RecommendationDto recommendation) {
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
    }

    private void createSkillOffer(
            RecommendationDto recommendation,
            SkillOfferDto skillOffer,
            List<Skill> receiverSkills) {
        var existedSkill = receiverSkills.stream()
                .filter(x -> x.getId() == skillOffer.getSkillId())
                .findFirst();
        if (existedSkill.isPresent()
                && userSkillGuaranteeRepository.findByGuarantorId(recommendation.getAuthorId()).isEmpty()) {
            userSkillGuaranteeRepository.create(
                    recommendation.getReceiverId(),
                    skillOffer.getSkillId(),
                    recommendation.getAuthorId());
        }

        skillOfferRepository.create(skillOffer.getSkillId(), recommendation.getId());
    }
}
