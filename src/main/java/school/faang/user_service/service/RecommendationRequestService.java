package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.RequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RequestFilter> filters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        validateRecommendationRequest(recommendationRequestDto);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        User requester = userRepository.findById(recommendationRequestDto.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        User receiver = userRepository.findById(recommendationRequestDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        List<SkillRequest> savedSkills = saveSkills(recommendationRequestDto.getSkillIds(), recommendationRequest);

        recommendationRequest.getSkills().addAll(savedSkills);
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();

        for (RequestFilter requestFilter : filters) {
            if (requestFilter.isApplicable(filter)) {
                requests = requestFilter.apply(requests, filter);
            }
        }

        return requests.map(recommendationRequestMapper::toDto).toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request with id " + id + " not found"));

        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));

        if (recommendationRequest.getStatus() == RequestStatus.ACCEPTED ||
                recommendationRequest.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalArgumentException("Cannot reject request, it is already processed");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(recommendationRequest);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    private void validateRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        if (!userRepository.existsById(recommendationRequestDto.getRequesterId())) {
            throw new IllegalArgumentException("Requester not found");
        }

        if (!userRepository.existsById(recommendationRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("Receiver not found");
        }

        Optional<RecommendationRequest> latestRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()
        );

        if (latestRequest.isPresent() && latestRequest.get().getCreatedAt().
                isAfter(LocalDateTime.now().minusMonths(6))) {
            throw new IllegalArgumentException("You can request a recommendation only once every 6 months");
        }

        for (Long skillId : recommendationRequestDto.getSkillIds()) {
            if (!skillRequestRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill with id " + skillId + " not found");
            }
        }
    }

    private List<SkillRequest> saveSkills(List<Long> skillIds, RecommendationRequest recommendationRequest) {
        List<SkillRequest> skillRequests = new ArrayList<>();

        for (Long skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new IllegalArgumentException("Skill with id " + skillId + " not found"));

            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setSkill(skill);
            skillRequest.setRequest(recommendationRequest);
            skillRequests.add(skillRequest);
        }

        skillRequestRepository.saveAll(skillRequests);
        return skillRequests;
    }
}
