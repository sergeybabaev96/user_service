package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.service.recommendation.RecommendationService;

/**
 * Контроллер для управления рекомендациями пользователей.
 * <p>
 * Этот класс отвечает за обработку запросов, связанных с рекомендациями, их валидацию
 * и передачу в сервисный слой для выполнения операций с данными.
 */
@Tag(
        name = "Recommendation controller",
        description = "Контроллер для управления рекомендациями"
)
@Slf4j
@RestController
@RequestMapping("/recommendation/")
@Validated
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Operation(
            summary = "Создает новую рекомендацию"
    )
    @PostMapping("{recommendationId}")
    public ResponseEntity<RecommendationViewDto> createRecommendation(
            @RequestBody @Valid RecommendationCreateDto recommendation,
            @PathVariable @NotNull Long recommendationId) {
        log.info("Creating new recommendation from author {} to receiver {}",
                recommendation.getAuthorId(), recommendation.getReceiverId());

        RecommendationViewDto created =
                recommendationService.create(recommendation, recommendationId);

        log.debug("Successfully created recommendation with id: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Обновляет уже существующую рекомендацию"
    )
    @PutMapping("{recommendationId}")
    public ResponseEntity<RecommendationViewDto> updateRecommendation(
            @Valid RecommendationCreateDto updated,
            @NotNull Long recommendationId) {
        log.info("Updating recommendation with id: {}", recommendationId);

        RecommendationViewDto recommendationViewDto =
                recommendationService.update(updated, recommendationId);

        log.debug("Successfully updated recommendation with id: {}", recommendationId);
        return ResponseEntity.ok(recommendationViewDto);
    }

    @Operation(
            summary = "Удаляет рекомендацию по ее айди"
    )
    @DeleteMapping("{recommendationId}")
    public ResponseEntity<Void> deleteRecommendation(@NotNull Long recommendationId) {
        log.info("Deleting recommendation with id: {}", recommendationId);

        recommendationService.delete(recommendationId);

        log.debug("Successfully deleted recommendation with id: {}", recommendationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получает все рекомендации пользователя"
    )
    @GetMapping("{receiverId}/{pageable}/get-all-user-recommendation")
    public ResponseEntity<Page<RecommendationViewDto>> getAllUserRecommendations(
            @PathVariable @NotNull Long receiverId,
            @RequestParam @NotNull Integer page,
            @RequestParam @NotNull Integer size) {
        log.info("Getting recommendations for user {}", receiverId);

        Pageable pageable = PageRequest.of(page, size);
        Page<RecommendationViewDto> result =
                recommendationService.getAllUserRecommendations(receiverId, pageable);

        log.debug("Found {} recommendations for user {}", result.getTotalElements(), receiverId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Получает все рекоменадации созданные пользователем"
    )
    @GetMapping("{authorId}/{pageable}/get-all-created-recommendation")
    public ResponseEntity<Page<RecommendationViewDto>> getAllCreatedRecommendation(
            @PathVariable @NotNull Long authorId,
            @RequestParam @NotNull Integer page,
            @RequestParam @NotNull Integer size) {
        log.info("Getting recommendations created by user {}", authorId);

        Pageable pageable = PageRequest.of(page, size);
        Page<RecommendationViewDto> result =
                recommendationService.getAllCreatedRecommendation(authorId, pageable);

        log.debug("Found {} recommendations created by user {}", result.getTotalElements(), authorId);
        return ResponseEntity.ok(result);
    }
}