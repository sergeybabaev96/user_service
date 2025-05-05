package school.faang.user_service.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.RecommendationRequestException;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestBaseMapper {
    public static final String REQUESTER_NOT_FOUND = "Requester not found";
    public static final String RECEIVER_NOT_FOUND = "Receiver not found";
    public static final String SKILLS_MISSING_FROM_DATABASE = "one or more skills are missing from the database";

    private final UserService userService;
    private final SkillService skillService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto toDto(RecommendationRequest entity) {
        RecommendationRequestDto dto = recommendationRequestMapper.toDto(entity);
        entity.getSkills().forEach(skillRequest -> dto.addSkill(skillRequest.getId()));
        dto.setRequesterId(entity.getRequester().getId());
        dto.setReceiverId(entity.getReceiver().getId());
        return dto;
    }

    public RecommendationRequest toEntity(RecommendationRequestDto dto) {
        RecommendationRequest entity = recommendationRequestMapper.toEntity(dto);
        //REQUESTER. Если нет в БД, то ошибка
        entity.setRequester(getUser(dto.getRequesterId(), REQUESTER_NOT_FOUND));
        //RECEIVER. Если нет в БД, то ошибка
        entity.setReceiver(getUser(dto.getReceiverId(), RECEIVER_NOT_FOUND));
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            entity.setStatus(RequestStatus.PENDING);
        } else {
            entity.setStatus(RequestStatus.valueOf(dto.getStatus()));
        }
        List<Skill> skills = skillService.getSkillsByIds(dto.getSkills());
        if (skills.size() != dto.getSkills().size()) {
            throw new RecommendationRequestException(SKILLS_MISSING_FROM_DATABASE);
        }

        skills.forEach(skill -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(entity);
            skillRequest.setSkill(skill);
            entity.addSkillRequest(skillRequest);
        });


        return entity;
    }

    private User getUser(Long userId, String errorMessage) {
        return userService.getUserById(userId)
            .orElseThrow(() -> {
                log.error(errorMessage);
                return new IllegalArgumentException(errorMessage);
            });
    }
}
