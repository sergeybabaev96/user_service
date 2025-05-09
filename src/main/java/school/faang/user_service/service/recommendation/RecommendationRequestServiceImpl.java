package school.faang.user_service.service.recommendation;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterBuilder;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillRequestService;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Data
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestService skillRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public Optional<RecommendationRequestDto> create(RecommendationRequestDto recommendationRequestDto) {
        Optional<RecommendationRequest> optionalRecommendationRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId());

        RecommendationRequest recommendationRequest = optionalRecommendationRequest.orElseThrow(() ->
                new NoSuchElementException("Either requester or receiver are not present in data bases"));

        if (recommendationRequest.getUpdatedAt().isBefore(LocalDateTime.now().minus(Period.ofMonths(6)))) {
            boolean allSkillsExist = skillRequestService.skillRequestsExist(recommendationRequest.getSkills());

            if (allSkillsExist) {
                recommendationRequestRepository.save(recommendationRequest);

                // Зачем повторно создавать скилы в базе данных если до этого мы проверяли их наличие в бд???
                skillRequestService.createSkillRequestsFromList(recommendationRequest.getSkills());

                return Optional.ofNullable(recommendationRequestMapper.toDto(recommendationRequest));
            }
        }

        return Optional.empty();
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> recommendations = recommendationRequestRepository.findAll();
        List<RecommendationRequestFilterStrategy> filterStrategies = RecommendationRequestFilterBuilder.buildStrategies(filter);

        List<RecommendationRequest> filteredRecommendations = recommendations.stream()
                .filter(recommendationRequest ->
                        filterStrategies.stream()
                                .allMatch(strategy -> strategy.filter(recommendationRequest)))
                .toList();

        return filteredRecommendations.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id).orElseThrow(NoSuchElementException::new));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User was not found"));
        if (recommendationRequest.getStatus() == RequestStatus.PENDING) {
            recommendationRequest.setStatus(RequestStatus.REJECTED);
            recommendationRequest.setRejectionReason(rejection.getReason());

            return recommendationRequestMapper.toDto(recommendationRequest);
        }

        return null;
    }
}
