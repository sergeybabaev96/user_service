package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestDto;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    @Value("${recommendationRequestMinDistanceMonths}")
    private int recommendationRequestMinDistanceMonths;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;

    RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        validateRecommendationRequest(recommendationRequest);
    }

    private void validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        if (!userService.existsById(recommendationRequest.requesterId())) {
            throw new DataValidationException(String.format(
                    "Requester with id %d is not found",
                    recommendationRequest.requesterId()));
        }

        if (!userService.existsById(recommendationRequest.receiverId())) {
            throw new DataValidationException(String.format(
                    "Receiver with id %d is not found",
                    recommendationRequest.receiverId()));
        }

        recommendationRequestRepository.findLatestPendingRequest()
    }
}
