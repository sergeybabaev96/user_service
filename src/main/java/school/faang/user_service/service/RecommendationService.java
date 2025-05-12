package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final SkillOfferMapper skillOfferMapper;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) throws DataValidationException {
        Optional<Recommendation> previousRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthor(), recommendationDto.getReceiver());

        if (previousRecommendation.isPresent()) {
            if (previousRecommendation.get().getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new DataValidationException("Recommendation was created too soon after previous");
            }
        }
        if (recommendationDto.getSkillOffers().size() != skillRepository.countExisting(recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkill)
                .toList())) {
            throw new DataValidationException("Some skills do not exist in our system");
        }

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
            if (recommendation.getReceiver().getSkills().contains(skillOfferMapper.toEntity(skillOfferDto).getSkill())) {
                userSkillGuaranteeRepository
                        .save(new UserSkillGuarantee(null,
                                recommendation.getReceiver(),
                                skillOfferMapper.toEntity(skillOfferDto).getSkill(),
                                recommendation.getAuthor()));
            }
            skillOfferRepository.save(skillOfferMapper.toEntity(skillOfferDto));
        });

        recommendationRepository
                .create(recommendationDto.getAuthor(), recommendationDto.getReceiver(), recommendationDto.getContent());
        return recommendationDto;
    }
}
