package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.UserSkillGuaranteeService;
import school.faang.user_service.service.skilloffer.SkillOfferService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final SkillOfferService skillOfferService;
    private final UserSkillGuaranteeService userSkillGuaranteeService;

    @Transactional
    public Recommendation create(Long authorId, Long receiverId,
                                 List<SkillOfferDto> skillOffersId, String content,
                                 LocalDateTime createdAt) {
        Recommendation recommendation = buildRecommendation(authorId, receiverId, skillOffersId, content, createdAt);
        checkSixMonthAgo(recommendation);
        checkExistSkillsOffer(recommendation);
        saveSkillOffers(recommendation);
        addGuarantorIfSkillExists(authorId, receiverId, recommendation);
        return recommendationRepository.save(recommendation);
    }

    @Transactional
    public Recommendation update(Long authorId, Long receiverId,
                                 List<SkillOfferDto> skillOffersId, String content, LocalDateTime createdAt) {
        Recommendation recommendation = buildRecommendation(authorId, receiverId, skillOffersId,
                content, createdAt);
        checkSixMonthAgo(recommendation);
        checkExistSkillsOffer(recommendation);
        recommendationRepository.update(recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(), recommendation.getContent());

        skillOfferService.deleteSkillOffer(recommendation.getId());

        saveSkillOffers(recommendation);
        addGuarantorIfSkillExists(authorId, receiverId, recommendation);
        return recommendation;
    }

    @Transactional
    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    @Transactional
    public List<Recommendation> getAllUserRecommendations(long receiverId) {
        return recommendationRepository.findAllByReceiverId(receiverId);
    }

    @Transactional
    public List<Recommendation> getAllGivenRecommendations(long authorId) {
        return recommendationRepository.findAllByAuthorId(authorId);
    }

    private Recommendation buildRecommendation(Long authorId, Long receiverId,
                                               List<SkillOfferDto> skillOffersId, String content,
                                               LocalDateTime createdAt) {
        Recommendation recommendation = new Recommendation();
        User author = userService.getUser(authorId);
        User receiver = userService.getUser(receiverId);
        List<SkillOffer> skillOffers = skillOfferService.getSkillOffers(skillOffersId, receiverId);
        recommendation.setContent(content);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setSkillOffers(skillOffers);
        recommendation.setCreatedAt(createdAt);
        return recommendation;
    }

    private void checkSixMonthAgo(Recommendation recommendation) {
        boolean lastRecommendationOpt = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthor().getId(),
                        recommendation.getReceiver().getId())
                .filter(rec -> rec.getCreatedAt().isBefore(recommendation.getCreatedAt().plusMonths(6L)))
                .isPresent();
        if (lastRecommendationOpt) {
            throw new DataValidationException("It hasn't been 6 months yet");
        }
    }

    private void checkExistSkillsOffer(Recommendation recommendation) {
        recommendation.getSkillOffers().forEach(skillOffer -> {
            if (!skillService.skillExistsByTitle(skillOffer.skill.getTitle())) {
                throw new DataValidationException("Skill %s do not exist".formatted(skillOffer.skill.getTitle()));
            }
        });
    }

    private void addGuarantorIfSkillExists(Long authorId, Long receiverId, Recommendation recommendation) {
        User receiver = userService.getUser(receiverId);

        List<Long> skillIdsOfRecommendation = recommendation.getSkillOffers().stream()
                .map(skillOffer -> skillOffer.getSkill().getId())
                .toList();

        receiver.getSkills().forEach(receiverSkill -> {
            if (skillIdsOfRecommendation.contains(receiverSkill.getId()) &&
                    !receiverSkill.getGuarantees().stream().
                            map(userSkillGuarantee -> userSkillGuarantee.getUser().getId())
                            .toList().contains(recommendation.getAuthor().getId())) {
                userSkillGuaranteeService.saveUserSkillGuarantee(receiverId, receiverSkill, authorId);
            }
        });
    }

    private void saveSkillOffers(Recommendation recommendation) {
        recommendation.getSkillOffers().
                forEach(skillOffer -> skillOfferService.create(skillOffer.getId(), recommendation.getId()));
    }
}
