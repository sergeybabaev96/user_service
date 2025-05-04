package school.faang.user_service.validator.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestValidationTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRequestRepository requestRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private RequestValidation requestValidation;

    private final List<Long> SKILL_IDS = List.of(10L, 20L);
    private final List<Skill> SKILLS = List.of(
            buildSkill(10L, "Java"),
            buildSkill(20L, "Spring")
    );

    @Test
    void validateRequest() {
        RecommendationRequestDto dto = buildValidDto();

        when(requestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(skillRepository.findAllById(SKILL_IDS))
                .thenReturn(SKILLS);

        List<Skill> result = requestValidation.validateRequest(dto);

        assertEquals(2, result.size());
        verify(userValidator, times(2)).checkUserExistsById(anyLong());
    }

    @Test
    void validateRequestNullDto() {
        assertThrows(IllegalArgumentException.class,
                () -> requestValidation.validateRequest(null));
    }

    @Test
    void validateRequestEmptyMessage() {
        RecommendationRequestDto dto = buildValidDto();
        dto.setMessage("   ");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> requestValidation.validateRequest(dto));

        assertTrue(exception.getMessage().contains("message is empty"));
    }

    @Test
    void validateRequestInvalidReceiver() {
        RecommendationRequestDto dto = buildValidDto();
        Long INVALID_USER_ID = 999L;
        dto.setReceiverId(INVALID_USER_ID);

        doThrow(new EntityNotFoundException("User not found"))
                .when(userValidator).checkUserExistsById(INVALID_USER_ID);

        assertThrows(EntityNotFoundException.class,
                () -> requestValidation.validateRequest(dto));
    }

    @Test
    void validateRequestExistingRecentRequest() {
        RecommendationRequestDto dto = buildValidDto();
        RecommendationRequest existingRequest = new RecommendationRequest();
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));

        when(requestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(existingRequest));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> requestValidation.validateRequest(dto));

        assertTrue(exception.getMessage().contains("already existed"));
    }

    @Test
    void validateRequestMissingSkills() {
        RecommendationRequestDto dto = buildValidDto();
        dto.setSkillsIds(List.of(10L, 30L));

        when(skillRepository.findAllById(dto.getSkillsIds()))
                .thenReturn(List.of(SKILLS.get(0)));

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> requestValidation.validateRequest(dto));

        assertTrue(exception.getMessage().contains("Missing skill IDs: [30]"));
    }

    private RecommendationRequestDto buildValidDto() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        Long VALID_USER_ID = 1L;
        requestDto.setRequesterId(VALID_USER_ID);
        requestDto.setReceiverId(VALID_USER_ID);
        requestDto.setMessage("Please recommend me");
        requestDto.setSkillsIds(SKILL_IDS);
        return requestDto;
    }

    private Skill buildSkill(Long skillId, String skillTitle) {
        return Skill.builder()
                .id(skillId)
                .title(skillTitle)
                .users(new ArrayList<>())
                .guarantees(new ArrayList<>())
                .events(new ArrayList<>())
                .goals(new ArrayList<>())
                .createdAt(LocalDateTime.now()) // Опционально, обычно автоматически
                .updatedAt(LocalDateTime.now()) // Опционально
                .build();
    }
}