package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Tag(name = "Запрос рекоммендации")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/recommendation-requests")
@Validated
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @Operation(summary = "Запросить рекомендацию")
    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody @Valid RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.requestRecommendation(recommendationRequest);
    }

    @Operation(summary = "Получить запросы рекоммендаций с фильтрами")
    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public List<RecommendationRequestDto> getRecommendationRequests(@PathVariable @Min(0) int pageNumber,
                                                                    @PathVariable @Min(1) int pageSize,
                                                                    RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRecommendationRequests(filter, pageNumber, pageSize);
    }

    @Operation(summary = "Получить рекоммендацию оп ID")
    @GetMapping("/{id}")
    public RecommendationRequestDto getRequestById(@PathVariable @Min(1) long id) {
        return recommendationRequestService.getRequestById(id);
    }

    @Operation(summary = "Отклонить запрос на рекоммендацию")
    @PostMapping("/reject/{id}")
    public void rejectRequest(@PathVariable @Min(1) long id, @RequestBody RejectionDto rejection) {
        recommendationRequestService.rejectRequest(id, rejection);
    }
}
