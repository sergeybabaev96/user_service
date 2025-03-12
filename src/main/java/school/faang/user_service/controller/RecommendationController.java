package school.faang.user_service.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.service.RecommendationService;

/**
 * Контроллер для управления рекомендациями пользователей.
 * <p>
 * Этот класс отвечает за обработку запросов, связанных с рекомендациями, их валидацию
 * и передачу в сервисный слой для выполнения операций с данными.
 * </p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *     <li>{@link #createRecommendation(RecommendationCreateDto, Long) Создание новой рекомендации} с проверкой валидности данных.</li>
 *     <li>{@link #updateRecommendation(RecommendationCreateDto, Long) Обновление существующей рекомендации}.</li>
 *     <li>{@link #deleteRecommendation(long) Удаление рекомендации} по её идентификатору.</li>
 *     <li>{@link #getAllUserRecommendations(long, Pageable ) Получение списка всех рекомендаций}, полученных пользователем.</li>
 *     <li>{@link #getAllCreatedRecommendation(long, Pageable) Получение списка всех рекомендаций}, созданных пользователем.</li>
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
     * @param recommendationId айди рекомендации
     * @return созданная рекомендация
     */
    public RecommendationViewDto createRecommendation(@NonNull RecommendationCreateDto recommendation,
                                                      @NonNull Long recommendationId) {
        return recommendationService.create(recommendation, recommendationId);
    }

    /**
     * Обновляет существующую рекомендацию после проверки валидности данных.
     *
     * @param updated DTO обновленной рекомендации
     * @param recommendationId айди рекомендации
     * @return обновленная рекомендация
     */
    public RecommendationViewDto updateRecommendation(@NonNull RecommendationCreateDto updated,
                                                      @NonNull Long recommendationId) {
        return recommendationService.update(updated, recommendationId);
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
     * @return Page рекомендаций
     */
    public Page<RecommendationViewDto> getAllUserRecommendations(long receiverId,
                                                                 @NonNull Pageable pageable) {
        return recommendationService.getAllUserRecommendations(receiverId, pageable);
    }

    /**
     * Получает все рекомендации, созданные пользователем.
     *
     * @param authorId идентификатор пользователя
     * @return Page рекомендаций
     */
    public Page<RecommendationViewDto> getAllCreatedRecommendation(long authorId,
                                                                   @NonNull Pageable pageable) {
        return recommendationService.getAllCreatedRecommendation(authorId,pageable);
    }
}
