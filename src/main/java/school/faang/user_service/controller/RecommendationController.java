package school.faang.user_service.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;
import java.util.Objects;

/**
 * Контроллер для управления рекомендациями пользователей.
 * <p>
 * Этот класс отвечает за обработку запросов, связанных с рекомендациями, их валидацию
 * и передачу в сервисный слой для выполнения операций с данными.
 * </p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *     <li>{@link #giveRecommendation(RecommendationCreateDto) Создание новой рекомендации} с проверкой валидности данных.</li>
 *     <li>{@link #updateRecommendation(RecommendationCreateDto) Обновление существующей рекомендации}.</li>
 *     <li>{@link #deleteRecommendation(long) Удаление рекомендации} по её идентификатору.</li>
 *     <li>{@link #getAllUserRecommendations(long) Получение списка всех рекомендаций}, полученных пользователем.</li>
 *     <li>{@link #getAllGivenRecommendations(long) Получение списка всех рекомендаций}, созданных пользователем.</li>
 * </ul>
 * </p>
 * <p>
 * Валидация включает в себя проверки на совпадение автора и получателя,
 * а также на наличие и содержимое текста рекомендации.
 * </p>
 *
 * @author marsel_mkh
 * @see RecommendationViewDto
 * @see RecommendationCreateDto
 * @see RecommendationService
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    /**
     * Создаёт новую рекомендацию после проверки валидности данных.
     *
     * @param recommendation DTO для создания рекомендации
     * @return созданная рекомендация
     */
    public RecommendationViewDto giveRecommendation(@NonNull RecommendationCreateDto recommendation) {
        validationRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }
    /**
     * Обновляет существующую рекомендацию после проверки валидности данных.
     *
     * @param updated DTO обновленной рекомендации
     * @return обновленная рекомендация
     */
    public RecommendationViewDto updateRecommendation(@NonNull RecommendationCreateDto updated) {
        validationRecommendation(updated);
        return recommendationService.update(updated);
    }
    /**
     * Удаляет рекомендацию по её идентификатору.
     *
     * @param recommendationId идентификатор рекомендации
     */
    public void deleteRecommendation(long recommendationId) {
        recommendationService.delete(recommendationId);
    }
    /**
     * Получает все рекомендации, полученные пользователем.
     *
     * @param receiverId идентификатор пользователя
     * @return список рекомендаций
     */
    public List<RecommendationViewDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }
    /**
     * Получает все рекомендации, созданные пользователем.
     *
     * @param authorId идентификатор пользователя
     * @return список рекомендаций
     */
    public List<RecommendationViewDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
    /**
     * Проверяет валидность рекомендации.
     *
     * @param recommendation DTO рекомендации
     * @throws DataValidationException если данные некорректны
     */
    private void validationRecommendation(RecommendationCreateDto recommendation) {
        if (Objects.equals(recommendation.getAuthorId(), recommendation.getReceiverId())) {
            log.error("authorId и receiverId не могут быть одинаковы");
            throw new DataValidationException("Author and receiver can not be identical");
        }
        if (recommendation.getContent() == null) {
            log.error("Рекомендация = null");
            throw new DataValidationException("Recommendation content cannot be null");
        }
        if (recommendation.getContent().isEmpty()) {
            log.error("Рекомендация пустая");
            throw new DataValidationException("Recommendation content cannot be empty");
        }
    }
}
