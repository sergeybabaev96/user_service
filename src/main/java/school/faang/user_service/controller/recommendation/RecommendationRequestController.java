package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @PostMapping
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(@RequestBody RecommendationRequestDto requestDto) {
        var recommendationRequest = recommendationRequestMapper.toEntity(requestDto);
        var createdRequest = recommendationRequestService.create(recommendationRequest);
        var responseDto = recommendationRequestMapper.toDto(createdRequest);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        var requests = recommendationRequestService.getRequests(filter);
        var response = requests.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable long id) {
        var request = recommendationRequestService.getRequest(id);
        var responseDto = recommendationRequestMapper.toDto(request);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejectionDto) {
        var rejectedRequest = recommendationRequestService.rejectRequest(id, rejectionDto.getReason());
        var responseDto = recommendationRequestMapper.toDto(rejectedRequest);
        return ResponseEntity.ok(responseDto);
    }
}