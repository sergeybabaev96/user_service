package school.faang.user_service.service.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestEvent;
import school.faang.user_service.dto.recommendation.RecommendationRequestMapper;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.KafkaProduceException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.kafka.producer.KafkaProducer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<Filter<RecommendationRequest, RecommendationRequestFilterDto>> recommendationRequestFilters;
    private final KafkaProducer<RecommendationRequestEvent> kafkaProducer;

    @Transactional
    @Override
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto dto) {
        checkExistsSkills(dto.getSkillsIds());
        checkRecommendationRequest(dto);
        RecommendationRequest recommendationRequest = buildRecommendationRequest(dto);
        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        for (Long skillId : dto.getSkillsIds()) {
            skillRequestRepository.create(savedRecommendationRequest.getId(), skillId);
        }
        skillRequestRepository.create(savedRecommendationRequest.getId(), dto.getReceiverId());
        sendEventToKafka(new RecommendationRequestEvent(dto.getRequesterId(), dto.getReceiverId(), savedRecommendationRequest.getId()));
        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filters,
                                                                    int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll(pageable).toList();
        for (Filter<RecommendationRequest, RecommendationRequestFilterDto> filter : recommendationRequestFilters) {
            if (filter.isApplicable(filters)) {
                recommendationRequests = filter.apply(recommendationRequests, filters);
            }
        }
        return recommendationRequests.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    @Override
    public RecommendationRequestDto getRequestById(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Recommendation request with id = %d not found", id)
        ));
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    @Override
    public void rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Recommendation request with id = %d not found", id)
        ));
        RequestStatus status = recommendationRequest.getStatus();
        if (RequestStatus.REJECTED != status && RequestStatus.ACCEPTED != status) {
            recommendationRequest.setStatus(RequestStatus.REJECTED);
            recommendationRequest.setRejectionReason(rejection.reason());
            recommendationRequestRepository.save(recommendationRequest);
        }
    }

    private void checkExistsSkills(List<Long> skillsIds) {
        skillsIds.forEach(id -> {
            if (skillRepository.findById(id).isEmpty()) {
                throw new EntityNotFoundException(String.format("Skill with id = %d not found", id));
            }
        });
    }

    private void checkRecommendationRequest(RecommendationRequestDto dto) {
        recommendationRequestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                .ifPresent((req) -> {
                    LocalDateTime now = LocalDateTime.now();
                    Period period = Period.between(now.toLocalDate(), req.getCreatedAt().toLocalDate());
                    if (period.getMonths() > 6) {
                        throw new IllegalArgumentException(
                                "A recommendation request has already been sent to this user within the last 6 months");
                    }
                });
    }

    private RecommendationRequest buildRecommendationRequest(RecommendationRequestDto dto) {
        User requesterUser = userRepository.findById(dto.getRequesterId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Requester user with id = %d not found", dto.getRequesterId())));
        User recieverUser = userRepository.findById(dto.getReceiverId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Receiver user with id = %d not found", dto.getReceiverId())));
        List<SkillRequest> skills = skillRequestRepository.findAllById(dto.getSkillsIds());
        return RecommendationRequest.builder()
                .message(dto.getMessage())
                .requester(requesterUser)
                .receiver(recieverUser)
                .skills(skills)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void sendEventToKafka(RecommendationRequestEvent event) {
        try {
            kafkaProducer.produce(event);
        } catch (JsonProcessingException e) {
            throw new KafkaProduceException(
                    String.format("Failed kafka produce recommendation request event. Request id = %d", event.getRequestId()));
        }
    }
}
