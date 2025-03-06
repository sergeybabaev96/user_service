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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto update(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);
        Iterable<SkillOffer> skillOffersIterable = skillOfferRepository.findAll();
        List<SkillOffer> allSkillOffers = new ArrayList<>();
        skillOffersIterable.forEach(allSkillOffers::add);

        ValidationRecommendationUtils.validateRecommendationContent(recommendationDto);
        ValidationRecommendationUtils.validateRecommendationDate(recommendationDto);
        ValidationRecommendationUtils.validateSkills(recommendationDto, allSkillOffers);

        updateRecommendation(recommendation);
        deleteAllAndCreate(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    private void deleteAllAndCreate(Recommendation recommendation) {
        List<SkillOffer> skillOffersOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
            if (skillOffersOfReceiver.contains(skillOffer)) {
                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                userSkillGuarantee.setUser(recommendation.getReceiver());
                userSkillGuarantee.setGuarantor(recommendation.getAuthor());
                userSkillGuarantee.setSkill(skillOffer.getSkill());
                skillOffer.getSkill().setGuarantees(List.of(userSkillGuarantee));
                userSkillGuaranteeRepository.save(userSkillGuarantee);
            }
        }
    }

    private void updateRecommendation(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        String content = recommendation.getContent();
        if (authorId == null) {
             throw new DataValidationException("Author id can't be null");
        } else if (receiverId == null) {
            throw new DataValidationException("Author id can't be null");
        }

        recommendationRepository.update(authorId, receiverId, content);
    }
}
