package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestValidation {

    private static final int SIX_MONTH_RECOMMENDATION_LIMIT = 6;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestRepository requestRepository;


    public List<Skill> validateRequest(RecommendationRequestDto dto) {

        if (dto == null) {
            throw new BusinessException("Запрос не может быть Null");
        }

        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new BusinessException("Ошибка проверки: сообщение пустое или null. DTO: " + dto);
        }

        if (!userRepository.existsById(dto.getRequesterId()) || !userRepository.existsById(dto.getReceiverId())) {
            throw new BusinessException("Ошибка проверки: Один или два юзера не существуют. RequesterId: "
                    + dto.getRequesterId() + ". ReceiverId: " + dto.getReceiverId());
        }

        if (requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(SIX_MONTH_RECOMMENDATION_LIMIT)))
                .isPresent()) {
            throw new BusinessException("Запрос уже существует в течение последних 6 месяцев DTO: " + dto);
        }

        List<Long> skillIds = dto.getSkillsIds();
        if (skillIds == null) {
            return List.of();
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);

        if (skills.size() != skillIds.size()) {
            throw new BusinessException("Некоторые навыки не существуют. Предоставленные ID навыков: " + skillIds);
        }
        return skills;
    }
}
