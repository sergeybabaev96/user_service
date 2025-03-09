package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validationUtils.RecommendationValidation;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        RecommendationValidation.validateRecommendationContent(recommendationDto.getContent());
        RecommendationValidation.validateSkills(recommendationDto, skillOfferRepository.findAllSkillOffers());
        LocalDateTime lastRecommendation = recommendationRepository.
                findLastRecommendationDate(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        RecommendationValidation.validateRecommendationDate(lastRecommendation);

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);
        recommendation.setCreatedAt(LocalDateTime.now());

        Long recommendationId = createRecommendation(recommendation);
        recommendation.setId(recommendationId);

        createSkillOffer(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    private void createSkillOffer(Recommendation recommendation) {
        List<SkillOffer> skillOfferListOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());
        List<SkillOffer> skillOffersOfRecommendation = recommendation.getSkillOffers();
        for (SkillOffer skillOffer : skillOffersOfRecommendation) {
            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
            if (skillOfferListOfReceiver.contains(skillOffer)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(recommendation.getReceiver());
                userSkillGuarantee.setSkill(skillOffer.getSkill());
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
                skillOffer.getSkill().setGuarantees(List.of(userSkillGuarantee));
                userSkillGuaranteeRepository.save(userSkillGuarantee);
            }
        }
    }

    private Long createRecommendation(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        if (authorId == null) {
            throw new DataValidationException("Id of author can't be null");
        } else if (receiverId == null) {
            throw new DataValidationException("Id of receiver can't be null");
        }
        String content = recommendation.getContent();
        return recommendationRepository.create(authorId, receiverId, content);
    }
}
