package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferMapper skillOfferMapper;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        List<SkillOffer> skillOffers = recommendation.getSkillOffers().stream()
                .map(skillOfferMapper::toEntity).toList();
        if (checkSixMonths(recommendation) && !checkSkillExist(recommendation)) {
            for (SkillOffer skill : skillOffers) {
                skillOfferRepository.create(skill.getSkill().getId(), skill.getRecommendation().getId());
            }
        }
        addGuarantor(recommendation);
        recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        saveSkillOffers(recommendation);
        return recommendation;
    }

    private boolean checkSixMonths(RecommendationDto recommendation) {
        Optional<Recommendation> optional = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(), recommendation.getReceiverId());
        if (optional.isPresent()) {
            Recommendation lastRecommendation = optional.get();
            if (ChronoUnit.MONTHS.between(lastRecommendation.getCreatedAt(), LocalDateTime.now()) < 6) {
                throw new DataValidationException("6 months has not passed yet");
            }
        }
        return true;
    }

    private boolean checkSkillExist(RecommendationDto recommendation) {
        for (SkillOfferDto skill : recommendation.getSkillOffers()) {
            if (!skillRepository.existsById(skill.getSkillId())) {
                throw new DataValidationException("Skill is not in the database");
            }
        }
        return true;
    }

    private void addGuarantor(RecommendationDto recommendation) {
        List<Skill> receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        Optional<User> receiverOptional = userRepository.findById(recommendation.getAuthorId());
        User receiver = receiverOptional.get();
        Optional<User> guarantorOptional = userRepository.findById(recommendation.getReceiverId());
        User guarantor = guarantorOptional.get();
        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
            for (Skill receiverSkill : receiverSkills) {
                if (skillOffer.getSkillId() == receiverSkill.getId()) {
                    userSkillGuarantee.setGuarantor(guarantor);
                    userSkillGuarantee.setSkill(receiverSkill);
                    userSkillGuarantee.setUser(receiver);
                }
            }
        }
    }

    private void saveSkillOffers(RecommendationDto recommendation) {
        List<SkillOffer> skillOffers = recommendation.getSkillOffers().stream()
                .map(skillOfferMapper::toEntity).toList();
        for (SkillOffer skillOffer : skillOffers) {
            skillOfferRepository.save(skillOffer);
        }
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        Recommendation entityRecommendation = recommendationMapper.toEntity(recommendation);
        if (checkSkillExist(recommendation) && checkSixMonths(recommendation)) {
            recommendationRepository.update(entityRecommendation.getAuthor().getId(), entityRecommendation.getReceiver().getId(), entityRecommendation.getContent());
            skillOfferRepository.deleteAllByRecommendationId(entityRecommendation.getId());
            for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
                skillOfferRepository.create(skillOffer.getSkillId(), entityRecommendation.getId());
            }
            addGuarantor(recommendation);
        }
        return recommendation;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged()).toList();
        return recommendations.stream()
                .map(recommendationMapper::toDto).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()).toList();
        return recommendations.stream()
                .map(recommendationMapper::toDto).toList();
    }
}