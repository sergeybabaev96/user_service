package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/recommendation-request")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestRcvDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("requestDto cannot be null");
        }
        if (StringUtils.isBlank(requestDto.message())) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        return recommendationRequestService.createRequest(requestDto);
    }

    @PostMapping("/search")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filters) {
        if (filters == null) {
            throw new IllegalArgumentException("filters cannot be null");
        }
        return recommendationRequestService.getRequests(filters);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PatchMapping("/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejectionDto) {
        if (rejectionDto == null) {
            throw new IllegalArgumentException("rejectionDto cannot be null");
        }
        if (StringUtils.isBlank(rejectionDto.reason())) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        return recommendationRequestService.rejectRequest(id, rejectionDto);
    }
}
