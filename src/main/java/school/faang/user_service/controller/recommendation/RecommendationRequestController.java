package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation-requests")
@Tag(name = "Recommendation requests API", description = "API для управления запросами на рекомендации")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/new")
    @Operation(summary = "Запросить рекомендацию",
            description = "Создает запрос на рекомендацию, на основе переданных данных")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().trim().isBlank()) {
            throw new IllegalArgumentException("Recommendation message cannot be null or empty.");
        }
        recommendationRequestService.create(recommendationRequest);
        return recommendationRequest;
    }

    @PostMapping("/requests")
    @Operation(summary = "Найти рекомендации",
            description = "Находит все рекомендации и выводит их на основе переданного фильтра")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getFilteredRecommendationRequests(filter);
    }

    @GetMapping("/request-{id}")
    @Operation(summary = "Найти рекомендацию", description = "Выводит рекомендацию с переданным идентификатором")
    public RecommendationRequestDto getRecommendationRequest(
            @Parameter(description = "Идентификатор запроса") @PathVariable long id) {
        return recommendationRequestService.getRecommendationRequestById(id);
    }

    @PostMapping("/request-{id}")
    @Operation(summary = "Отклонить запрос на рекомендацию",
            description = "Отклоняет запрос на рекомендацию с переданным идентификатором, на основе переданных данных")
    public RecommendationRequestDto rejectRequest(@Parameter(description = "Идентификатор запроса")
                                                      @PathVariable long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);

    }
}
