package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> filters;
    private final UserService userService;
    private final SkillRequestService skillRequestService;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        User requester = userService.getUserById(dto.requesterId());
        User receiver = userService.getUserById(dto.receiverId());


        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        Optional<RecommendationRequest> existingRequest = recommendationRequestRepository
                .findByRequesterAndReceiverAndCreatedDateAfter(requester, receiver, sixMonthsAgo);

        if (existingRequest.isPresent()) {
            throw new IllegalStateException("Recommendation request already sent within the last 6 months.");
        }
        RecommendationRequest request = recommendationRequestRepository
                .create(requester.getId(), receiver.getId(), dto.message());
        request.setSkills(skillRequestService.findByIds(dto.skillsId()));

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
        RecommendationRequest request = recommendationRequestRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Recommendation request with ID " + id + " not found"));
        if (request.getStatus().equals(RequestStatus.PENDING)) {
            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(rejection.reason());
            recommendationRequestRepository.save(request);
        }
        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

}




