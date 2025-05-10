package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exceptions.RecommendationRequestException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestBaseMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    public static final String SIX_MONTHS_PERIOD_ERROR = "A recommendation request from the same user " +
        "to another can be sent no more than once every 6 months.";

    private final RecommendationRequestRepository requestRepository;
    private final RecommendationRequestBaseMapper mapper;

    @Override
    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        validateTimePeriod(dto);
        RecommendationRequest entity = mapper.toEntity(dto);
        RecommendationRequest resultEntity = requestRepository.save(entity);
        return mapper.toDto(resultEntity);
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        return List.of();
    }

    /**
     * Метод проверяет, что запрос рекомендации от одного и того же пользователя к другому можно
     * отправлять не чаще, чем один раз в 6 месяцев.
     */
    public void validateTimePeriod(RecommendationRequestDto dto) {
        int requestCount = requestRepository.countRepeatedRequest(dto.getRequesterId(), dto.getReceiverId());
        if (requestCount > 0) {
            throw new RecommendationRequestException(SIX_MONTHS_PERIOD_ERROR);
        }
    }
}
