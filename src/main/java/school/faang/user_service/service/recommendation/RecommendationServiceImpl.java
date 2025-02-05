package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private static final int VALID_MONTH = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository guaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        Long createdRecommendationId = recommendationRepository.create(recommendation.getAuthorId(),
                recommendation.getReceiverId(), recommendation.getContent());
        recommendation.setId(createdRecommendationId);
        createSkillOffersByExistSkills(recommendation);

        return recommendation;
    }

    @Override
    public RecommendationDto update(RecommendationDto recommendation) {
        if (recommendation.getId() == null) {
            throw new DataValidationException("RecommendationId cannot be null!");
        }
        validateRecommendation(recommendation);
        recommendationRepository.update(recommendation.getAuthorId(),
                recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        createSkillOffersByExistSkills(recommendation);
        return recommendation;
    }

    @Override
    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    @Override
    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        if (recommendations == null || recommendations.isEmpty()) {
            return Page.empty(pageable);
        }
        return recommendations.map(recommendation -> recommendationMapper.toDto(recommendation));
    }


    @Override
    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);
        if (recommendations == null || recommendations.isEmpty()) {
            return Page.empty(pageable);
        }
        return recommendations.map(recommendation -> recommendationMapper.toDto(recommendation));
    }

    private void createSkillOffersByExistSkills(RecommendationDto recommendation) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        if (skillOffers != null) {
            List<SkillOfferDto> existSkills = existSkillsInSystem(skillOffers);
            validateSkills(existSkills);
            recommendation.setSkillOffers(existSkills);
            List<Long> offerIds = createOffers(recommendation);

            if (offerIds.isEmpty() || offerIds.size() != skillOffers.size()) {
                throw new DataValidationException("Mismatch in skill offers for the recommendation");
            }
            for (int i = 0; i < skillOffers.size(); i++) {
                skillOffers.get(i).setId(offerIds.get(i));
            }
        }
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (!isValidRecommendation(recommendation)) {
            throw new DataValidationException("You have already recommended this user");
        }
    }

    private void validateSkills(List<SkillOfferDto> existSkills) {
        if (existSkills == null || existSkills.isEmpty()) {
            throw new DataValidationException("These skills do not exist in the system");
        }
    }

    private boolean isValidRecommendation(RecommendationDto recommendation) {
        return recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthorId(), recommendation.getReceiverId())
                .map(lastRecommendation -> isRecommendationValid(lastRecommendation, recommendation))
                .orElse(true);
    }

    private boolean isRecommendationValid(Recommendation lastRecommendation, RecommendationDto recommendation) {
        LocalDateTime createdAt = lastRecommendation.getCreatedAt();
        long monthsBetween = ChronoUnit.MONTHS.between(createdAt, recommendation.getCreatedAt());
        return monthsBetween >= VALID_MONTH;
    }

    private List<SkillOfferDto> existSkillsInSystem(List<SkillOfferDto> skillOffers) {
        List<Skill> skills = skillRepository.findAll();
        if (skills.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> allSkillIds = skills.stream().map(Skill::getId).toList();
        return skillOffers.stream().filter(skill -> allSkillIds.contains(skill.getSkillId()))
                .toList();
    }

    private List<Long> createOffers(RecommendationDto recommendation) {
        Long receiverId = recommendation.getReceiverId();
        Long userId = recommendation.getAuthorId();
        Long recommendationId = recommendation.getId();
        List<Long> offerIds = new ArrayList<>();
        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
            List<SkillOffer> findOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillOffer.getSkillId(), receiverId);
            Long offerId = processSkillOffer(findOffersOfSkill, skillOffer, userId, receiverId, recommendationId);
            offerIds.add(offerId);
        }
        return offerIds;
    }

    private Long processSkillOffer(List<SkillOffer> findOffersOfSkill, SkillOfferDto skillOffer, Long userId, Long receiverId, Long recommendationId) {
        if (isNotEmpty(findOffersOfSkill)) {
            handleGuarantee(findOffersOfSkill, skillOffer, userId, receiverId);
            return findMatchingOfferId(findOffersOfSkill, recommendationId);
        } else {
            return skillOfferRepository.create(skillOffer.getSkillId(), recommendationId);
        }
    }

    private void handleGuarantee(List<SkillOffer> findOffersOfSkill, SkillOfferDto skillOffer, Long userId, Long receiverId) {
        if (!isGuaranteeExist(userId, receiverId, skillOffer.getSkillId())) {
            guaranteeRepository.create(receiverId, skillOffer.getSkillId(), userId);
        }
    }

    private Long findMatchingOfferId(List<SkillOffer> findOffersOfSkill, Long recommendationId) {
        return findOffersOfSkill.stream()
                .sorted(Comparator.comparing((SkillOffer offer) -> offer.getRecommendation().getId()).reversed())
                .map(SkillOffer::getId)
                .findFirst()
                .orElseThrow(() -> new DataValidationException("No offer found with the given recommendation ID"));
    }

    private boolean isNotEmpty(List<SkillOffer> findOffersOfSkill) {
        return !findOffersOfSkill.isEmpty();
    }

    private boolean isGuaranteeExist(Long userId, Long receiverId, Long skillId) {
        Iterable<UserSkillGuarantee> guarantees = guaranteeRepository.findAll();
        if (!guarantees.iterator().hasNext()) {
            return false;
        }
        return StreamSupport.stream(guarantees.spliterator(), false)
                .anyMatch(guarantee -> userId.equals(guarantee.getGuarantor().getId())
                        && receiverId.equals(guarantee.getUser().getId())
                        && skillId.equals(guarantee.getSkill().getId()));
    }
}
