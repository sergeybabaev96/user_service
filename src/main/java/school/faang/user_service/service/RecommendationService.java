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
import school.faang.user_service.utils.ValidationRecommendationUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        List<SkillOffer> allSkillOffers = skillOfferRepository.findAllSkillOffers();

        ValidationRecommendationUtils.validateRecommendation(recommendationDto);
        ValidationRecommendationUtils.validateRecommendationDate(recommendationDto);
        ValidationRecommendationUtils.validateSkills(recommendationDto, allSkillOffers);

        createInSkillOfferRepository(recommendation);
        addGuarantee(recommendation);
        createInRecommendationRepository(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    private void createInSkillOfferRepository(Recommendation recommendation) {
        List<SkillOffer> skillOffersOfRecommendation = recommendation.getSkillOffers();
        for (SkillOffer skillOffer : skillOffersOfRecommendation) {
            if (skillOffer == null) {
                throw new DataValidationException("Skill offer can't be null");
            }
            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
        }
    }

    private void createInRecommendationRepository(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        if (authorId == null) {
            throw new DataValidationException("Id of author can't be null");
        } else if (receiverId == null) {
            throw new DataValidationException("Id of receiver can't be null");
        }
        String content = recommendation.getContent();
        recommendationRepository.create(authorId, receiverId, content);
    }

    private void addGuarantee(Recommendation recommendation) {
        List<SkillOffer> skillOfferListOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());
        for (SkillOffer skillOfferOfRecommendation : recommendation.getSkillOffers()) {
            if (skillOfferListOfReceiver.contains(skillOfferOfRecommendation)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(recommendation.getReceiver());
                userSkillGuarantee.setSkill(skillOfferOfRecommendation.getSkill());
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
                userSkillGuaranteeRepository.save(userSkillGuarantee);
            }
        }
    }
}
