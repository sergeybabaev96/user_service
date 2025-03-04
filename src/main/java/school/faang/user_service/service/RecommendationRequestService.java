package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;

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
        request.setRequester(requester); // Устанавливаем запрашивающего
        request.setReceiver(receiver); // Устанавливаем получателя
        request.setCreatedAt(LocalDateTime.now() ); // Устанавливаем дату создания запроса

        request = recommendationRequestRepository.save(request);
        return recommendationRequestMapper.toRecommendationRequestDto(request);

    }
}




