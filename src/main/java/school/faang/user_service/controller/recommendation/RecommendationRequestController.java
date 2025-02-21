package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.request.RecommendationRequestDto;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.RequestFilterDto;
import school.faang.user_service.dto.respones.RecommendationResponseDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/recommendation-requests")
@RestController
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationResponseDto requestRecommendation(@Valid @RequestBody RecommendationRequestDto rec) {
        RecommendationRequest entity = recommendationRequestService.create(
                rec.requesterId(), rec.receiverId(), rec.message(), rec.skillsIds());
        return mapper.toDto(entity);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RecommendationResponseDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        List<RecommendationRequest> requests = recommendationRequestService.getAllRequests(filter);
        return mapper.toDtoList(requests);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecommendationResponseDto getRecommendationById(@PathVariable(name = "id") Long recommendationId) {
        RecommendationRequest entity = recommendationRequestService.getRequestById(recommendationId);
        return mapper.toDto(entity);
    }

    @PostMapping("/reject/{id}")
    public RecommendationResponseDto rejectRequest(@PathVariable(name = "id") Long recommendationId,
                                                   @Valid @RequestBody
                                                   RejectionDto rejection) {
        RecommendationRequest entity = recommendationRequestService.rejectRequest(recommendationId, rejection);
        return mapper.toDto(entity);
    }
}