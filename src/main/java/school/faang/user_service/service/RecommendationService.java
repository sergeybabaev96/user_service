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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserRepository userRepository;
    private final UserSkillGuarantee userSkillGuarantee;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        if (validateTime(recommendationDto) && validateSkillExist(recommendationDto)) {
            saveSkillOffers(recommendationDto);
            addGuarantee(recommendationDto);
            recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        }
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        if (validateTime(recommendationDto) && validateSkillExist(recommendationDto)) {
            recommendationRepository.update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
            recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
                skillOfferRepository.deleteAllByRecommendationId(skillOfferDto.getRecommendationId());
            });
            recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
                skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId());
            });
            addGuarantee(recommendationDto);
        }
        return recommendationDto;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged()).toList();
        return recommendations.stream().map(recommendationMapper::toDto).toList();
    }

    private boolean validateTime(RecommendationDto recommendationDto) {
        Optional<Recommendation> optional = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        if (optional.isPresent()) {
            Recommendation lastRecommendation = optional.get();
            return ChronoUnit.MONTHS
                    .between(lastRecommendation.getCreatedAt(), recommendationDto.getCreatedAt()) > 6;
        } else {
            throw new DataValidationException("6 months have not passed");
        }
    }

    private boolean validateSkillExist(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
            if (!skillRepository.existsById(skillOfferDto.getSkillId())) {
                throw new DataValidationException("Skill doesn't exist in database");
            }
        });
        return true;
    }

    private void saveSkillOffers(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
            skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId());
        });
    }

    public void addGuarantee(RecommendationDto recommendationDto) {
        List<Skill> receiverSkills = skillRepository.findAllByUserId(recommendationDto.getReceiverId());
        Optional<User> optionalReceiver = userRepository.findById(recommendationDto.getReceiverId());
        Optional<User> optionalGarantor = userRepository.findById(recommendationDto.getAuthorId());

        for (SkillOfferDto skill : recommendationDto.getSkillOffers()) {
            for (Skill receiverSkill : receiverSkills) {
                if (skill.getSkillId() == receiverSkill.getId()) {
                    optionalReceiver.ifPresent(userSkillGuarantee::setUser);
                    optionalGarantor.ifPresent(userSkillGuarantee::setGuarantor);
                    userSkillGuarantee.setSkill(receiverSkill);
                }
            }
        }
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()).toList();
        return recommendations.stream().map(recommendationMapper::toDto).toList();
    }
}
