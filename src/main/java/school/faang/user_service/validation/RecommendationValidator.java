package school.faang.user_service.validation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    /**
     * Проверяет рекомендацию на соответствие всем правилам валидации.
     *
     * @param recommendation - объект DTO с рекомендацией
     * @throws DataValidationException если рекомендация не прошла валидацию
     */
    public void validate(@NonNull RecommendationCreateDto recommendation) {
        validateRecommendationTimeInterval(recommendation);
        validateSkillOffers(recommendation);
        validateAuthorAndReceiver(recommendation);

    }

    /**
     * Проверяет, что предлагаемые в рекомендации навыки существуют в системе.
     *
     * @param recommendation - объект DTO с рекомендацией
     * @throws DataValidationException если навыки не найдены
     */
    private void validateSkillOffers(RecommendationCreateDto recommendation) {
        List<SkillOfferCreateDto> skillOffers = recommendation.getSkillOffers();
        if (skillOffers == null || skillOffers.isEmpty()) {
            log.error("Ошибка валидации: отсутствуют навыки в рекомендации");
            throw new DataValidationException("Skill offer is not found");
        }

        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferCreateDto::getSkillId)
                .toList();
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                log.error("Навык с ID {} не найден в системе", skillId);
                throw new DataValidationException("Skill offer not found");
            }
        }
    }

    /**
     * Проверяет, что автор дает рекомендацию не раньше,
     * чем через 6 месяцев после его последней рекомендации этому пользователю.
     *
     * @param recommendation - объект DTO с рекомендацией
     * @throws DataValidationException если рекомендация обновлена слишком рано
     */
    private void validateRecommendationTimeInterval(RecommendationCreateDto recommendation) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        LocalDateTime lastRecommendationDate = getUpdateAtCreateRecommendation(recommendation)
                .orElseThrow(() -> new DataValidationException("Update date is not found"));

        if (lastRecommendationDate.isBefore(sixMonthsAgo)) {
            log.error("Ошибка валидации: рекомендация обновлена слишком рано");
            throw new DataValidationException("Updated recommendation too early");
        }
    }

    /**
     * Проверяет, что authorId и receiverId не совпадают.
     *
     * @param recommendation - объект DTO с рекомендацией
     * @throws DataValidationException если authorId и receiverId совпадают
     */
    public void validateAuthorAndReceiver(RecommendationCreateDto recommendation) {
        if (Objects.equals(recommendation.getAuthorId(), recommendation.getReceiverId())) {
            log.error("authorId и receiverId не могут быть одинаковы");
            throw new DataValidationException("Author and receiver can not be identical");
        }
    }

    /**
     * Получает дату последнего обновления рекомендации для данного автора и получателя.
     *
     * @param recommendation - объект DTO с рекомендацией
     * @return Optional<LocalDateTime> - дата последнего обновления рекомендации
     */
    private Optional<LocalDateTime> getUpdateAtCreateRecommendation(RecommendationCreateDto recommendation) {
        User recommendationAuthor  = getUser(recommendation.getAuthorId());
        return recommendationAuthor.getRecommendationsGiven().stream()
                .filter(rec -> {
                    User receiver = rec.getReceiver();
                    User createRecommendationReceiver = getUser(receiver.getId());
                    return receiver.equals(createRecommendationReceiver);
                })
                .findFirst()
                .map(Recommendation::getUpdatedAt);
    }

    /**
     * Получает пользователя по его идентификатору
     * @param userId - идентификатор пользователя
     * @return User - найденный пользователь
     * @throws DataValidationException если пользователь не найден
     */
    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: пользователь с ID {} не найден", userId);
            return new EntityNotFoundException("User is not found");
        });
    }
}
