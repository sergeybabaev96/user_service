package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exceptions.RecommendationRequestException;
import school.faang.user_service.filters.Filter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestBaseMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.text.MessageFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    public static final String SIX_MONTHS_PERIOD_ERROR = "A recommendation request from the same user " +
            "to another can be sent no more than once every 6 months.";
    public static final String REQUEST_BY_ID_NOT_FOUND = "The recommendation request by id={0} was not found.";

    private final RecommendationRequestRepository requestRepository;
    private final RecommendationRequestBaseMapper mapper;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;

    @Override
    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        validateTimePeriod(dto);
        RecommendationRequest entity = mapper.toEntity(dto);
        RecommendationRequest resultEntity = requestRepository.save(entity);
        return mapper.toDto(resultEntity);
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> requests = requestRepository.findAll().stream();
        for (var filter : filters) {
            if (filter.isApplicable(filterDto)) {
                filter.apply(requests, filterDto);
            }
        }
        return requests
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = MessageFormat.format(REQUEST_BY_ID_NOT_FOUND, id);
                    log.error(errorMessage);
                    return new RecommendationRequestException(errorMessage);
                });
        return mapper.toDto(entity);
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
