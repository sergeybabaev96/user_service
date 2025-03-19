package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final static int COUNT_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
        /*В методе create(recommendation) нужно проверить, что автор дает рекомендацию не раньше,
        чем через 6 месяцев после его последней рекомендации этому пользователю.
        Также проверить, что навыки, предлагаемые в рекомендации существуют в системе.
         */
        LocalDate monthsAgo = LocalDate.now().minusMonths(COUNT_MONTHS);

        User receiver = userRepository.getReferenceById(recommendation.getReceiverId());

        boolean isRightTimeRecommendation = receiver.getRecommendationsReceived().stream()
                .filter(recommendationReceived ->
                        recommendationReceived.getAuthor().getId().equals(recommendation.getAuthorId()))
                .noneMatch(recommendationReceived ->
                        recommendationReceived.getCreatedAt().isAfter(monthsAgo.atStartOfDay()));

        boolean isRightSkillRecommendation = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkill)
                .allMatch(skill -> skillRepository.existsById(skill.getId()));

        if (!isRightSkillRecommendation) {
            throw new DataValidationException("Навык(и), предлагаемый(ые) в рекомендации не существует(ют)");
        } else if (!isRightTimeRecommendation) {
            throw new DataValidationException("Автор дает рекомендацию раньше," +
                    "чем через 6 месяцев после его последней рекомендации этому пользователю.");
        } else {
            /*Если у пользователя, которому дают рекомендацию, такой скилл уже есть,
            то добавить автора рекомендации гарантом к скиллу, который он предлагает,
            если этот автор еще не стоит там гарантом.*/
            recommendation.getSkillOffers()
                    .forEach(skillOfferDto ->
                            skillOfferRepository.create(skillOfferDto.getSkill().getId(),
                                    skillOfferDto.getRecommendation().getId()));

        }
        return saveSkillOffers(recommendation);
    }

    private RecommendationDto saveSkillOffers(RecommendationDto recommendationDto) {
        recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        return recommendationDto;
    }
}
