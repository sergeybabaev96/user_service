package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Service
@AllArgsConstructor
@Transactional
public class RecommendationService {
    private static final String SKILLS_PRESENCE_ERROR = "Skills are not presented in system ";
    private static final String RECOMMENDATION_FREQUENCY_ERROR = "recommendation was given less than 6 months ago";
    private static final String RECOMMENDATION_NOT_FOUND = "recommendation is not found in DB";
    @Autowired
    private final RecommendationRepository recommendationRepository;
    @Autowired
    private final SkillOfferRepository skillOfferRepository;
    @Autowired
    private final SkillRepository skillRepository;
    @Autowired
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Autowired
    private final RecommendationMapper recommendationMapper;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final int RESTRICTION_TIME_PERIOD_MONTHS = 6;


    public Recommendation create(Recommendation recommendation) {

        restrictionTimePeriodForNewRecommendationIsOver(recommendation);
        skillsArePresentedInSystem(recommendation.getSkillOffers());

        Long newRecommendationId = saveRecommendation(recommendation);
        saveSkillOffers(recommendation.getSkillOffers(), newRecommendationId);
        addSkillsAndAddGuarantor(recommendation, newRecommendationId);

        Optional<Recommendation> recommendationFromDataBase = recommendationRepository.findById(newRecommendationId);
        return recommendationFromDataBase.get();
    }

    /**
     * - updates only recommendation content of "recommendation" table
     * - Does not update guarantors of skills - doesn't delete lines from "user_skill_guarantee" table.(To brainstorm)
     * - Does not update skill offers from "skill_offer" table to keep the table consistent
     * - Does not update skills from "user_skill" table to keep the table consistent
     *
     * @param recommendation - recommendation to update
     */
    @Transactional
    public Recommendation update(Recommendation recommendation) {

        restrictionTimePeriodForNewRecommendationIsOver(recommendation);
        skillsArePresentedInSystem(recommendation.getSkillOffers());
        updateRecommendation(recommendation);

        Optional<Recommendation> recommendationFromDataBase = recommendationRepository.findById(recommendation.getId());
        return recommendationFromDataBase.get();
    }

    /**
     * - Removes recommendation from "recommendation" table
     * - Removes skill offers from "skill_offer" table due to hibernate annotation
     * - Does not remove guarantor from skills - doesn't delete lines from "user_skill_guarantee" table.(To brainstorm)
     * - Does not remove skills from "user_skill" table to keep the table consistent
     *
     * @param id - id of recommendation to remove
     */
    @Transactional
    public void delete(long id) {
        Optional<Recommendation> optional = recommendationRepository.findById(id);
        if (optional.isPresent()) {
            recommendationRepository.deleteById(id);
        } else {
            throw new DataValidationException(RECOMMENDATION_NOT_FOUND);
        }
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {

        List<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId);
        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        List<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId);
        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private boolean skillsArePresentedInSystem(@NonNull List<SkillOffer> skillOffers) {
        for (SkillOffer skill : skillOffers) {
            if (!skillRepository.existsByTitle(skill.getSkill().getTitle())) {
                throw new DataValidationException(SKILLS_PRESENCE_ERROR);
            }
        }
        return true;
    }

    @Transactional
    private void addSkillsAndAddGuarantor(Recommendation recommendation, long newRecommendationId) {
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        if (!skillOffers.isEmpty()) {
            List<Skill> userOldSkills = skillRepository.findAllByUserId(recommendation.getReceiver().getId());
            List<Long> userOldSkillsIds = userOldSkills.stream()
                    .map(Skill::getId)
                    .toList();

            List<Skill> allSkillsGuaranteedToUserByGuarantee = userSkillGuaranteeRepository
                    .findAllSkillsGuaranteedToUserByGuarantee(
                            recommendation.getReceiver().getId(),
                            recommendation.getAuthor().getId());
            List<Long> allSkillsGuaranteedToUserByGuaranteeIds = allSkillsGuaranteedToUserByGuarantee.stream()
                    .map(Skill::getId)
                    .toList();

            for (SkillOffer skillOffer : skillOffers) {
                if (userOldSkillsIds.contains(skillOffer.getSkill().getId())) {
                    if (!allSkillsGuaranteedToUserByGuaranteeIds.contains(skillOffer.getSkill().getId())) {
                        addGuarantorToSkill(recommendation, skillOffer);
                    }
                } else {
                    skillRepository.assignSkillToUser(skillOffer.getSkill().getId(), recommendation.getReceiver().getId());
                    addGuarantorToSkill(recommendation, skillOffer);
                }
            }
        }
    }

    /**
     * @param recommendation - an object of recommendation received from Controller class
     * @return - true if last recommendation from DB given by recommendation.authorId to recommendation.receiverId
     * was created more than number of months ago set RESTRICTION_TIME_PERIOD_MONTHS
     * - false if last recommendation from DB given by recommendation.authorId to recommendation.receiverId
     * was created less than number of months ago set RESTRICTION_TIME_PERIOD_MONTHS
     */
    private boolean restrictionTimePeriodForNewRecommendationIsOver(Recommendation recommendation) {
        Optional<Recommendation> recommendationFromDB = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId());
        Optional<LocalDateTime> timeOfLastRecommendationGivenToReceiver = recommendationFromDB.map(Recommendation::getUpdatedAt);
        if (timeOfLastRecommendationGivenToReceiver.isEmpty()) {
            return true;
        }
        if (LocalDateTime.now().minusMonths(RESTRICTION_TIME_PERIOD_MONTHS)
                .isAfter(timeOfLastRecommendationGivenToReceiver.get())) {
            return true;
        } else {
            throw new DataValidationException(RECOMMENDATION_FREQUENCY_ERROR);
        }
    }

    private Long saveRecommendation(Recommendation recommendation) {
        return recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent());
    }

    private void saveSkillOffers(List<SkillOffer> skillOffers, Long newRecommendationId) {
        if (skillOffers != null) {
            for (SkillOffer skillOffer : skillOffers) {
                skillOfferRepository.create(skillOffer.getSkill().getId(), newRecommendationId);
            }
        }
    }

    private void updateRecommendation(Recommendation recommendation) {
        recommendationRepository.updateByRecommendationId(
                recommendation.getId(),
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent());
    }

    @Transactional
    private void addGuarantorToSkill(Recommendation recommendation, SkillOffer skillOffer) {
        userSkillGuaranteeRepository.save(new UserSkillGuarantee(
                null,
                recommendation.getReceiver(),
                skillOffer.getSkill(),
                recommendation.getAuthor()));
    }
}
