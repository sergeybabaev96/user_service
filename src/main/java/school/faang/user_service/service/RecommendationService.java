package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    public RecommendationService(RecommendationRepository recommendationRepository, SkillOfferRepository skillOfferRepository) {
        this.recommendationRepository = recommendationRepository;
        this.skillOfferRepository = skillOfferRepository;
    }


    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = RecommendationMapper.INSTANCE.toEntity(recommendationDto);
        Recommendation oldRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId()
        ).orElseThrow(EntityNotFoundException::new);
        if(oldRecommendation.getCreatedAt().plusMonths(6).isAfter(recommendation.getCreatedAt())) {
            throw new DataValidationException(
                    "the author " + recommendation.getAuthor().getId()
                            + " gives a recommendation " + recommendation.getId()
                            + " earlier than 6 months after his last recommendation to this user"
            );
        }
        recommendation.getSkillOffers().forEach(
                skillOffer -> {

                    if (!skillOfferRepository.existsById(skillOffer.getId())) {
                        throw new DataValidationException("Skill" + skillOffer.getSkill().getId() + " does not exist");
                    }

                }
        );
        Long createdRecommendationId = recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );
        skillOfferRepository.saveAll(recommendation.getSkillOffers());
        recommendation.getSkillOffers().forEach(
                skillOffer -> {
                    long skillId = skillOffer.getSkill().getId();
                    long receiverId = recommendation.getReceiver().getId();
                    if(skillOfferRepository.countAllOffersOfSkill(
                            skillId,
                            receiverId
                    ) == 0) {
                        skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
                    } else {
                        skillOfferRepository.findAllOffersOfSkill(skillId, receiverId).forEach(
                                oldSkillOffer -> oldSkillOffer.getSkill()
                                        .addGuarantee(recommendation.getReceiver(), recommendation.getAuthor())
                        );
                    }
                }
        );
        return recommendationRepository.findById(createdRecommendationId)
                .map(RecommendationMapper.INSTANCE::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {

        Recommendation recommendation = RecommendationMapper.INSTANCE.toEntity(recommendationDto);
        Recommendation oldRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId()
        ).orElseThrow(EntityNotFoundException::new);
        if(oldRecommendation.getCreatedAt().plusMonths(6).isAfter(recommendation.getCreatedAt())) {
            throw new DataValidationException(
                    "the author " + recommendation.getAuthor().getId()
                            + " gives a recommendation " + recommendation.getId()
                            + " earlier than 6 months after his last recommendation to this user"
            );
        }
        recommendation.getSkillOffers().forEach(
                skillOffer -> {

                    if (!skillOfferRepository.existsById(skillOffer.getId())) {
                        throw new DataValidationException("Skill" + skillOffer.getSkill().getId() + " does not exist");
                    }

                }
        );
        recommendationRepository.update(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        recommendation.getSkillOffers().forEach(
                skillOffer -> {
                    skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
                }
        );

        return recommendationRepository.findById(recommendation.getId())
                .map(RecommendationMapper.INSTANCE::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return RecommendationMapper.INSTANCE.toDto(
                recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged()).getContent()
        );
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return RecommendationMapper.INSTANCE.toDto(
                recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()).getContent()
        );
    }
}
