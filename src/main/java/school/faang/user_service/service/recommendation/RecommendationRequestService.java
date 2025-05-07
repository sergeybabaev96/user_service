package school.faang.user_service.service.recommendation;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillRequestService;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@Data
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestService skillRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        Optional<RecommendationRequest> optionalRecommendationRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId());

        // 6. Если нет Receiver/Requester, то создаём свой из DTO
        RecommendationRequest recommendationRequest = optionalRecommendationRequest.orElse(recommendationRequestMapper.toEntity(recommendationRequestDto));

        // 6. Проверка на свежесть в пол года
        if (recommendationRequest.getUpdatedAt().isBefore(LocalDateTime.now().minus(Period.ofMonths(6)))) {
            // 7. Запрашиваемые скилы существуют в базе данных, вернет true если все скилы присутствуют
            boolean allSkillsExist = skillRequestService.skillRequestsExist(recommendationRequest.getSkills());

            if (allSkillsExist) {
                recommendationRequestRepository.create(recommendationRequest);

                // Зачем повторно создавать скилы в базе данных если до этого мы проверяли их наличие в бд???
                skillRequestService.createSkillRequestsFromList(recommendationRequest.getSkills());

                return recommendationRequestMapper.toDto(recommendationRequest);
            }
        }

        return null;
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> recommendations = recommendationRequestRepository.findAll();
        // При каждом изменении много ручного кода, может есть какой-то другой варик?
        // или проверять какие из фильтров null и игнорить?
        List<RecommendationRequest> filteredRecommendations = recommendations.stream()
                .filter(recommendationRequest -> {
                    boolean appropriate = true;

                    if (filter.getRequesterId() != null) {
                        appropriate &= Objects.equals(recommendationRequest.getRequester().getId(), filter.getRequesterId());
                    }
                    if (filter.getReceiverId() != null) {
                        appropriate &= Objects.equals(recommendationRequest.getRequester().getId(), filter.getReceiverId());
                    }
                    if (filter.getStatus() != null) {
                        appropriate &= Objects.equals(recommendationRequest.getStatus(), filter.getStatus());
                    }
                    if (filter.getSkillId() != null) {
                        appropriate &= recommendationRequest.getSkills().stream()
                                .anyMatch(skillRequest -> skillRequest.getSkill().getId() == filter.getSkillId());
                    }
                    if (filter.getCreatedAt() != null) {
                        appropriate &= Objects.equals(recommendationRequest.getCreatedAt(), filter.getCreatedAt());
                    }

                    return appropriate;
                })
                .toList();

        return filteredRecommendations.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id).orElseThrow(NoSuchElementException::new));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(NoSuchElementException::new);
        if (recommendationRequest.getStatus() == RequestStatus.PENDING) {
            recommendationRequest.setStatus(RequestStatus.REJECTED);
            recommendationRequest.setRejectionReason(rejection.getReason());

            return recommendationRequestMapper.toDto(recommendationRequest);
        }

        return null;
    }
}
