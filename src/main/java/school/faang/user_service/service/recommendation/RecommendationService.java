package school.faang.user_service.service.recommendation;

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
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
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
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private static final int MIN_MONTH_VALUE = 6;

    public RecommendationDto create(RecommendationDto recommendation) {
        List<SkillOffer> skillOffers = recommendation.getSkillOffers().stream()
                .map(skillOfferMapper::toEntity).toList();
        checkSixMonths(recommendation);
        checkSkillExist(recommendation);
        for (SkillOffer skill : skillOffers) {
                skillOfferRepository.create(skill.getSkill().getId(), skill.getRecommendation().getId());
        }
        addGuarantor(recommendation);
        Recommendation recommendationToCreate = recommendationMapper.toEntity(recommendation);
        recommendationRepository.create(recommendationToCreate.getAuthor().getId(), recommendationToCreate.getReceiver().getId(), recommendationToCreate.getContent());
        saveSkillOffers(recommendation);
        return recommendationMapper.toDto(recommendationToCreate);
    }

    private void checkSixMonths(RecommendationDto recommendation) {
        Optional<Recommendation> optional = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(), recommendation.getReceiverId());
        if (optional.isPresent()) {
            Recommendation lastRecommendation = optional.get();
            if (ChronoUnit.MONTHS.between(lastRecommendation.getCreatedAt(), LocalDateTime.now()) < MIN_MONTH_VALUE) {
                throw new DataValidationException("Six months haven't passed yet");
            }
        }
    }

    private void checkSkillExist(RecommendationDto recommendation) {
        for (SkillOfferDto skill : recommendation.getSkillOffers()) {
            if (!skillRepository.existsById(skill.getSkillId())) {
                throw new DataValidationException("Skill is not in the database");
            }
        }
    }

    private void addGuarantor(RecommendationDto recommendation) {
        List<Skill> receiverSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        User receiver = userRepository.findById(recommendation.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Receiver does not exist"));
        User guarantor = userRepository.findById(recommendation.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Author does not exist"));
        recommendation.getSkillOffers().stream()
                .flatMap(skillOffer -> receiverSkills.stream()
                        .filter(receiverSkill -> skillOffer.getSkillId() == receiverSkill.getId())
                        .map(receiverSkill -> {
                            userSkillGuarantee.setGuarantor(guarantor);
                            userSkillGuarantee.setSkill(receiverSkill);
                            userSkillGuarantee.setUser(receiver);
                            return userSkillGuarantee;
                        })
                )
                .forEach(userSkillGuaranteeRepository::save);
    }

    private void saveSkillOffers(RecommendationDto recommendation) {
        List<SkillOffer> skillOffers = recommendation.getSkillOffers().stream()
                .map(skillOfferMapper::toEntity).toList();
        skillOfferRepository.saveAll(skillOffers);
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        Recommendation entityRecommendation = recommendationMapper.toEntity(recommendation);
        checkSkillExist(recommendation);
        checkSixMonths(recommendation);
        recommendationRepository.update(entityRecommendation.getAuthor().getId(), entityRecommendation.getReceiver().getId(), entityRecommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(entityRecommendation.getId());
        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
                skillOfferRepository.create(skillOffer.getSkillId(), entityRecommendation.getId());
        }
        addGuarantor(recommendation);
        return recommendationMapper.toDto(entityRecommendation);
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