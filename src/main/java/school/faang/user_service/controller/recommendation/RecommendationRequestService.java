package school.faang.user_service.controller.recommendation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;

    public RecommendationRequestDto processRequest(RecommendationRequestDto requestDto) {
        RecommendationRequest entity = recommendationMapper.toEntity(requestDto);
        RecommendationRequest savedEntity = recommendationRequestRepository.save(entity);
        return recommendationMapper.toDto(savedEntity);
    }

    public RecommendationRequestDto create(@Valid RecommendationRequestDto recommendationRequest) {

        Long requesterId = recommendationRequest.getRequesterId();
        Long receiverId = recommendationRequest.getReceiverId();

        if (!userRepository.existsById(requesterId)) {
            throw new EntityNotFoundException("Requester not found with id: " + requesterId);
        }

        if (!userRepository.existsById(receiverId)) {
            throw new EntityNotFoundException("Receiver not found with id: " + receiverId);
        }

        Optional<RecommendationRequest> latestRequest = recommendationRequestRepository
                .findLatestPendingRequest(recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId());

        if (latestRequest.isPresent() && latestRequest.get().getCreatedAt()
                .isAfter(LocalDateTime.now().minusMonths(6))) {
            throw new DataValidationException("You can send recommendation request to this user only once per 6 months");
        }

        latestRequest.get().getSkills().stream()
                .peek(skillRequest -> {
                    if (!skillRepository.existsByTitle(skillRequest.getSkill().getTitle())) {
                        throw new DataValidationException("This skill no to DB!");
                    }
                })
                .peek(skillRequest -> skillRequestRepository.create(skillRequest.getId(), skillRequest.getSkill().getId()));


        RecommendationRequest savedRequest = recommendationRequestRepository.save(latestRequest.get());

        return recommendationMapper.toDto(latestRequest.get());
    }
}