package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exceptions.RecommendationRequestException;
import school.faang.user_service.filters.Filter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestBaseMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    public static final String SIX_MONTHS_PERIOD_ERROR = "A recommendation request from the same user " +
            "to another can be sent no more than once every 6 months.";
    public static final String REQUEST_BY_ID_NOT_FOUND = "The recommendation request by id={0} was not found.";
    public static final String STATUS_CANNOT_BE_CHANGED = "The recommendation request status cannot be changed. id={0}";

    private final RecommendationRequestRepository requestRepository;
    private final RecommendationRequestBaseMapper mapper;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;

    @Override
    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        validateTimePeriod(dto);
        RecommendationRequest entity = mapper.toEntity(dto);
        RecommendationRequest resultEntity = requestRepository.save(entity);
        return mapper.toDto(resultEntity);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> requestException(id, REQUEST_BY_ID_NOT_FOUND));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        RecommendationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> requestException(id, REQUEST_BY_ID_NOT_FOUND));
        log.info("before {}", entity);
        if (entity.getStatus().equals(RequestStatus.PENDING)) {
            Integer rowCount = requestRepository.setStatus(id, RequestStatus.REJECTED, rejection.getReason());
            if (rowCount != 1) {
                throw requestException(id, STATUS_CANNOT_BE_CHANGED);
            }
            RecommendationRequest result = requestRepository.findById(id)
                    .orElseThrow(() -> requestException(id, REQUEST_BY_ID_NOT_FOUND));
            log.info("after update {}", result);
            return mapper.toDto(result);
        } else {
            throw requestException(id, STATUS_CANNOT_BE_CHANGED);
        }
    }

    /**
     * Метод проверяет, что запрос рекомендации от одного и того же пользователя к другому можно
     * отправлять не чаще, чем один раз в 6 месяцев.
     */
    private void validateTimePeriod(RecommendationRequestDto dto) {
        int requestCount = requestRepository.countRepeatedRequest(dto.getRequesterId(), dto.getReceiverId());
        if (requestCount > 0) {
            throw requestException(SIX_MONTHS_PERIOD_ERROR);
        }
    }

    @NotNull
    private static RecommendationRequestException requestException(Long id, String message) {
        String errorMessage = MessageFormat.format(message, id);
        log.error(errorMessage);
        return new RecommendationRequestException(errorMessage);
    }

    @NotNull
    private static RecommendationRequestException requestException(String message) {
        log.error(message);
        return new RecommendationRequestException(message);
    }
}
