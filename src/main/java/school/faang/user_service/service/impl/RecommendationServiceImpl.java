package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    public static final Integer LAST_RECOMMENDATION_PERIOD = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final RecommendationMapper recommendationMapper;

    @Override
    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        checkForLastRecommendationPeriod(recommendation);

        Long recommendationId = recommendationRepository.create(recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        recommendation.setId(recommendationId);
        saveSkillOffers(recommendation);

        return recommendation;
    }

    @Override
    public RecommendationDto update(RecommendationDto updated) {
        validateRecommendation(updated);
        checkForLastRecommendationPeriod(updated);
        recommendationRepository.update(updated.getAuthorId(), updated.getReceiverId(), updated.getContent());
        skillOfferRepository.deleteAllByRecommendationId(updated.getId());

        for (SkillOfferDto skillOfferDto : updated.getSkillOffers()) {
            createSkillOffer(updated, skillOfferDto);
            saveSkillWithGuarantee(updated, skillOfferDto);
        }
        return updated;
    }

    public void delete(Long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private void saveSkillOffers(RecommendationDto recommendation) {
        Optional.ofNullable(recommendation.getSkillOffers())
                .orElse(Collections.emptyList())
                .forEach(skillOffer -> {
            createSkillOffer(recommendation, skillOffer);
            saveSkillWithGuarantee(recommendation, skillOffer);
        });
    }

    private void saveSkillWithGuarantee(RecommendationDto updated, SkillOfferDto skillOfferDto) {
        skillRepository.findUserSkill(skillOfferDto.getSkillId(), updated.getReceiverId()).ifPresent(skill -> {
            UserSkillGuarantee guarantee = userSkillGuaranteeMapper.toEntity(updated.getReceiverId(), updated.getAuthorId(), skill);
            skill.addGuarantee(guarantee);
            skillRepository.save(skill);
        });
    }

    private void createSkillOffer(RecommendationDto recommendation, SkillOfferDto skillOffer) {
        skillOfferRepository.create(skillOffer.getSkillId(), recommendation.getId());
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent().isBlank()) {
            throw new DataValidationException("Recommendation content is blank");
        }

        if (recommendation.getSkillOffers() != null && !recommendation.getSkillOffers().isEmpty()) {
            checkForExistingSkills(recommendation);
        }
    }

    private void checkForExistingSkills(RecommendationDto recommendation) {
        if (!recommendation.getSkillOffers()
                .stream()
                .allMatch(skillOfferDto -> skillRepository.existsById(skillOfferDto.getSkillId()))) {
            throw new EntityNotFoundException("These skills do not exists in system");
        }
    }

    private void checkForLastRecommendationPeriod(RecommendationDto recommendation) {
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(), recommendation.getReceiverId())
                .ifPresent(lastRecommendation -> {
                    if (lastRecommendation.getCreatedAt()
                            .plusMonths(LAST_RECOMMENDATION_PERIOD)
                            .isBefore(recommendation.getCreatedAt())) {
                        throw new EntityNotFoundException("Recommendation can only be given after 6 months.");
                    }
                });
    }
}
