package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final List<RecommendationRequestFilter> filters;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new NotFoundException("Requester with ID " + dto.getRequesterId() + " not found"));

        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new NotFoundException("Receiver with ID " + dto.getReceiverId() + " not found"));

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        Optional<RecommendationRequest> existingRequest = recommendationRequestRepository
                .findByRequesterAndReceiverAndCreatedDateAfter(requester, receiver, sixMonthsAgo);

        if (existingRequest.isPresent()) {
            throw new IllegalStateException("Recommendation request already sent within the last 6 months.");
        }

        RecommendationRequest request = recommendationRequestMapper.toRecommendationRequest(dto);

        recommendationRequestRepository.create(requester.getId()
                , request.getReceiver().getId(), request.getMessage());

        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .map(recommendationRequestMapper::toRecommendationRequestDto).orElseThrow(()
                        -> new NotFoundException("Recommendation request with ID " + id + " not found"));
    }

    public List<RecommendationRequestDto> getRequest(RequestFilterDto dto) {
        Stream<RecommendationRequest> recommendationRequestStream = recommendationRequestRepository.findAll().stream();
        for (RecommendationRequestFilter filter : filters) {
            if (filter.isApplicable(dto)) {
                recommendationRequestStream = filter.apply(recommendationRequestStream, dto);
            }
        }
        return recommendationRequestStream
                .map(recommendationRequestMapper::toRecommendationRequestDto)
                .toList();

    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        return null;
    }

}




