package school.faang.user_service.service.recommendation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.SkillRepository;
import java.time.LocalDateTime;



import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;


    public RecommendationRequest create(@NonNull RecommendationRequest recommendationRequest) {

        if (!userRepository.existsById(recommendationRequest.getRequester().getId())) {
            throw new IllegalArgumentException("Requester not found");
        }

        if (!userRepository.existsById(recommendationRequest.getReceiver().getId())) {
            throw new IllegalArgumentException("Receiver not found");
        }

        List<Long> skillIds = recommendationRequest.getSkills().stream()
                .map(skill -> skill.getId())
                .collect(Collectors.toList());

        if (skillRepository.countExisting(skillIds) < skillIds.size()) {
            throw new IllegalArgumentException("One or more skills do not exist");
        }

        Optional<RecommendationRequest> latestRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequest.getRequester().getId(),
                recommendationRequest.getReceiver().getId()
        );

        if (latestRequest.isPresent() && latestRequest.get().getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot request recommendation more than once in 6 months");
        }

        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);
        skillRequestRepository.saveAll(savedRequest.getSkills());

        return savedRequest;
    }


    public List<RecommendationRequest> getRequests(RequestFilterDto filter) {
        return recommendationRequestRepository.findAll().stream()
                .filter(request -> filter.getRequesterId() == null ||
                        Objects.equals(Optional.ofNullable(request.getRequester()).map(User::getId).orElse(null),
                                filter.getRequesterId()))
                .filter(request -> filter.getReceiverId() == null ||
                        Objects.equals(Optional.ofNullable(request.getReceiver()).map(User::getId).orElse(null),
                                filter.getReceiverId()))
                .collect(Collectors.toList());
    }

    public RecommendationRequest getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));
    }

    public RecommendationRequest rejectRequest(long id, String reason) {
        RecommendationRequest request = getRequest(id);

        if (request.getStatus() == RequestStatus.ACCEPTED || request.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalStateException("Cannot reject a request with status " + request.getStatus());
        }


        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        return recommendationRequestRepository.save(request);
    }
}
