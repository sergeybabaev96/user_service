package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.ValidationUtils;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final Random random = new Random();

    public RecommendationDto create(RecommendationDto recommendationDto) {
        ValidationUtils.validateRecommendationDate(recommendationDto);
        ValidationUtils.validateSkills(recommendationDto, skillOfferRepository);
        addGuarantee(recommendationDto);
        recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        for (Long skillOfferId : recommendationDto.getSkillOfferIds()) {

        }
        return recommendationDto;
    }

    private void addGuarantee(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        List<SkillOffer> recieverOfferSkillsList =
                skillOfferRepository.findAllByUserId(recommendationDto.getReceiverId());
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            if (recieverOfferSkillsList.contains(skillOffer)) {
                userSkillGuaranteeRepository.addGuarantor(random.nextLong(), recommendation.getReceiver(),
                        skillOffer.getSkill(), recommendation.getAuthor());
            }
        }
    }
}
