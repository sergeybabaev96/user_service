package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserValidator userValidator;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRepository skillRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto create(@NotNull RecommendationRequestDto recommendationRequestDto) {
        log.info("Создание запроса рекомендаций от пользователя с id {} для пользователя с id {}",
                recommendationRequestDto.getReceiverId(), recommendationRequestDto.getRequesterId());

        userValidator.validateUser(recommendationRequestDto.getRequesterId());
        userValidator.validateUser(recommendationRequestDto.getReceiverId());
        recommendationRequestValidator.validateRecommendation(recommendationRequestDto);


        RecommendationRequest request = recommendationRequestMapper.toEntity(recommendationRequestDto);

        RecommendationRequest finalRequest = request;
        List<SkillRequest> skillRequest = recommendationRequestDto.getSkillRequests().stream().map(skillRequestDto -> {
            Skill skill = getSkill(skillRequestDto.getSkillId());
            return SkillRequest.builder().request(finalRequest).skill(skill).build();
        }).toList();

        request.setSkills(skillRequest);

        request = recommendationRequestRepository.save(request);
        log.info("Запрос рекомендации с id {} успешно сохранен", request.getId());
        return recommendationRequestMapper.toDto(request);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto requestFilter) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        recommendationRequestFilters.stream().filter(filter -> filter.isApplicable(requestFilter)).forEach(filter ->
                filter.apply(recommendationRequests, requestFilter));
        log.info("Получение списка запросов рекомендаций после фильтрации");
        return recommendationRequestMapper.toDtoList(recommendationRequests.toList());
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest entity = recommendationRequestRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Запрос с " + id + " не найден"));

        return recommendationRequestMapper.toDto(entity);
    }

    public RecommendationRequestDto rejectRequest(Long id, @NotNull RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRecommendationFromBd(id);
        recommendationRequestValidator.checkRequestsStatus(id, recommendationRequest.getStatus());
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());
        recommendationRequestRepository.save(recommendationRequest);
        log.info("Запрос рекомендации с id {} был отклонен", id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    private Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId).orElseThrow(() -> {
            log.warn("Навык с id {} не найден", skillId);
            return new NoSuchElementException(String.format("Нет навыка с id = %d", skillId));
        });
    }


}
