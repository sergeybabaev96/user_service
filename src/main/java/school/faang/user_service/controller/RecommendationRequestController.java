package school.faang.user_service.controller;

import com.amazonaws.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.request.filter.RecommendationRequestFilterDto;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/recommendations")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @PostMapping("/request-recommendation")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null || StringUtils.isNullOrEmpty(recommendationRequest.getMessage())) {
            throw new IllegalArgumentException("Message must not be null");
        }
        return recommendationRequestMapper.toDto(recommendationRequestService.create(recommendationRequest));
    }

    @GetMapping("/filter")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RecommendationRequestFilterDto dto) {
        return recommendationRequestService.getRequestByFilter(dto);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestMapper.toDto(recommendationRequestService.getRequestById(id));
    }
}
