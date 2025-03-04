package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.ValidationUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;


    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        ValidationUtils.validateRecommendationDate(recommendation);
        ValidationUtils.validateSkills(recommendation, skillOfferRepository);

        createInSkillOfferRepository(recommendation);
        addGuarantee(recommendation);
        createInRecommendationRepository(recommendation);
        recommendation.getSkillOffers().forEach(skillOfferRepository::save);
        return recommendationDto;
    }

    private void createInSkillOfferRepository(Recommendation recommendation) {
        List<SkillOffer> skillOffersOfRecommendation = recommendation.getSkillOffers();
        skillOffersOfRecommendation.forEach(skillOffer ->
                skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId()));
    }

    private void createInRecommendationRepository(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        String content = recommendation.getContent();
        recommendationRepository.create(authorId, receiverId, content);
    }

    private void addGuarantee(Recommendation recommendation) {
        List<SkillOffer> skillOfferListOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());
        for (SkillOffer soOfRecommendation : recommendation.getSkillOffers()) {
            if (skillOfferListOfReceiver.contains(soOfRecommendation)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(recommendation.getReceiver());
                userSkillGuarantee.setSkill(soOfRecommendation.getSkill());
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
            }
        }
    }
}
