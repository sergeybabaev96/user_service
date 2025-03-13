package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Slf4j
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
            log.info("Автор {} даёт рекомендацию {} ранее, чем через 6 месяцев",
                    recommendation.getAuthor().getId(),
                    recommendation.getId()
            );
            throw new DataValidationException(
                    "the author %s gives a recommendation %d"
                            + " earlier than 6 months after his last recommendation to this user",
                    recommendation.getAuthor().getId(),
                    recommendation.getId()
            );
        }

        Long createdRecommendationId = recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        if (skillOffers == null || skillOffers.isEmpty()) {
            log.info("Список Предложений Навыков пуст");
            throw new DataValidationException("Skill offers is empty");
        }

        for (SkillOffer skillOffer : skillOffers) {
            skillRepository.findById(skillOffer.getSkill().getId())
                    .orElseThrow(
                            () -> {
                                log.info("Навык {} не существует", skillOffer.getSkill().getId());
                                return new DataValidationException(
                                        "Skill %s does not exist", skillOffer.getSkill().getId());
                            }
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
            log.info("Автор {} дал рекомендацию {} ранее, чем через 6 месяцев",
                    recommendation.getAuthor().getId(),
                    recommendation.getId()
            );
            throw new DataValidationException(
                    "the author %s gives a recommendation %d " +
                            "earlier than 6 months after his last recommendation to this user",
                    recommendation.getAuthor().getId(),
                    recommendation.getId()
            );
        }
        recommendationRepository.update(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            skillRepository.findById(skillOffer.getSkill().getId())
                    .orElseThrow(
                            () -> {
                                log.info("Навык {} не существует", skillOffer.getSkill().getId());
                                return new DataValidationException(
                                        "Skill %d does not exist", skillOffer.getSkill().getId());
                            }
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
            log.info("Рекомендация {} не содержит ни одно предложение навыка", recommendation.getId());
            throw new DataValidationException("Рекомендация должна содержать хотя бы один скилл.");
        }

        for (SkillOffer offer : skillOffers) {
            Skill skill = skillRepository.findById(offer.getSkill().getId())
                    .orElseThrow(() -> {
                        log.info("Навык {} не существует", offer.getSkill().getId());
                        return new DataValidationException("Скилл с ID %d не найден.", offer.getSkill().getId());
                    });

            boolean receiverHasSkill = receiver.getSkills().contains(skill);

            if (receiverHasSkill) {
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
                SkillOffer newSkillOffer = new SkillOffer();
                newSkillOffer.setSkill(skill);
                newSkillOffer.setRecommendation(recommendation);
                skillOfferRepository.save(newSkillOffer);
            }
        }
    }
}
