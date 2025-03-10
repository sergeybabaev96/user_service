package school.faang.user_service.validator.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidation {
    private static final int SIX_MONTH_RECOMMENDATION_LIMIT = 6;
    private final SkillRepository skillRepository;
    private final RecommendationRequestRepository requestRepository;
    private final UserValidator userValidator;

    public List<Skill> validateRequest(RecommendationRequestDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Request cannot be Null");
        }

        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new IllegalArgumentException("Validation error: message is empty or null. DTO: " + dto);
        }

        userValidator.checkUserExistsById(dto.getReceiverId());
        userValidator.checkUserExistsById(dto.getRequesterId());

        if (requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now()
                        .minusMonths(SIX_MONTH_RECOMMENDATION_LIMIT)))
                .isPresent()) {
            throw new IllegalStateException("The request has already existed for the last 6 months DTO: " + dto);
        }

        List<Long> skillIds = dto.getSkillsIds();
        if (skillIds == null) {
            return List.of();
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);

        if (skills.size() != skillIds.size()) {
            Set<Long> foundSkillIds = skills.stream().map(Skill::getId).collect(Collectors.toSet());
            Set<Long> missingSkillIds = skillIds.stream()
                    .filter(id -> !foundSkillIds.contains(id))
                    .collect(Collectors.toSet());
            throw new EntityNotFoundException("Some skills do not exist. Missing skill IDs: " + missingSkillIds);
        }
        return skills;
    }
}
