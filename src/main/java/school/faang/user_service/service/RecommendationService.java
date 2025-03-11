package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;


    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        Recommendation oldRecommendation =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthor().getId(),
                        recommendation.getReceiver().getId()
                ).orElseThrow(EntityNotFoundException::new);
        if (oldRecommendation.getCreatedAt().plusMonths(6).isAfter(recommendation.getCreatedAt())) {
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
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        if (skillOffers == null || skillOffers.isEmpty()) {
            throw new DataValidationException("Skill offers is empty");
        }

        for (SkillOffer skillOffer : skillOffers) {
            skillRepository.findById(skillOffer.getSkill().getId())
                    .orElseThrow(
                            () -> new DataValidationException(
                                    "Skill " + skillOffer.getSkill().getId() + " does not exist")
                    );

            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
            addGuarantee(recommendation);
        }
        return recommendationRepository.findById(createdRecommendationId)
                .map(recommendationMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        Recommendation oldRecommendation =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthor().getId(),
                        recommendation.getReceiver().getId()
                ).orElseThrow(EntityNotFoundException::new);
        if (oldRecommendation.getCreatedAt().plusMonths(6).isAfter(recommendation.getCreatedAt())) {
            throw new DataValidationException(
                    "the author %s gives a recommendation %d " +
                            "earlier than 6 months after his last recommendation to this user",
                    recommendation.getAuthor().getId(),
                    recommendation.getId()
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

        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            skillRepository.findById(skillOffer.getSkill().getId())
                    .orElseThrow(
                            () -> new DataValidationException(
                                    "Skill " + skillOffer.getSkill().getId() + " does not exist")
                    );

            skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
            addGuarantee(recommendation);
        }

        return recommendationRepository.findById(recommendation.getId())
                .map(recommendationMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationMapper.toDto(
                recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged()).getContent()
        );
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationMapper.toDto(
                recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged()).getContent()
        );
    }

    private void addGuarantee(Recommendation recommendation) {
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        if (skillOffers == null || skillOffers.isEmpty()) {
            throw new DataValidationException("Рекомендация должна содержать хотя бы один скилл.");
        }

        for (SkillOffer offer : skillOffers) {
            Skill skill = skillRepository.findById(offer.getSkill().getId())
                    .orElseThrow(() -> new DataValidationException("Скилл с ID " + offer.getSkill().getId() + " не найден."));

            // Проверяем, есть ли этот скилл у получателя рекомендации
            boolean receiverHasSkill = receiver.getSkills().contains(skill);

            if (receiverHasSkill) {
                // Если скилл у пользователя уже есть, добавляем автора как гаранта, если его там нет
                UserSkillGuarantee guarantee = userSkillGuaranteeRepository
                        .findByUserAndSkill(receiver, skill)
                        .orElse(null);

                if (guarantee == null) {
                    guarantee = new UserSkillGuarantee();
                    guarantee.setUser(receiver);
                    guarantee.setSkill(skill);
                    guarantee.setGuarantor(author);
                    userSkillGuaranteeRepository.save(guarantee);
                } else if (!guarantee.getGuarantor().equals(author)) {
                    guarantee.setGuarantor(author);
                    userSkillGuaranteeRepository.save(guarantee);
                }
            } else {
                // Если у пользователя нет скилла, создаём новый SkillOffer
                SkillOffer newSkillOffer = new SkillOffer();
                newSkillOffer.setSkill(skill);
                newSkillOffer.setRecommendation(recommendation);
                skillOfferRepository.save(newSkillOffer);
            }
        }
    }
}
