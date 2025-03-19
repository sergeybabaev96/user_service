package school.faang.user_service.validator.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
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
import static org.mockito.ArgumentMatchers.anyList;
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
    List<Long> skillIds;
    List<Skill> expected;
    RequestValidation requestValidation;
    RecommendationRequestDto dto;


    @BeforeEach
    public void setUp() {
        skillIds = new ArrayList<>();
        skillIds.add(11L);
        skillIds.add(22L);
        skillIds.add(33L);
        Skill skill = new Skill();
        skill.setId(skillIds.get(0));
        Skill skill2 = new Skill();
        skill.setId(skillIds.get(1));
        Skill skill3 = new Skill();
        skill.setId(skillIds.get(2));
        expected = new ArrayList<>();
        expected.add(skill);
        expected.add(skill2);
        expected.add(skill3);


        requestValidation = new RequestValidation(skillRepository, requestRepository, userValidator);

        dto = new RecommendationRequestDto();
        dto.setId(3L);
        dto.setMessage("message");
        dto.setStatus(RequestStatus.ACCEPTED);
        dto.setSkillsIds(skillIds);
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setCreatedAt(LocalDateTime.now().minusDays(3));
        dto.setUpdatedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    public void validateRequest() {
        when(skillRepository.findAllById(anyList())).thenReturn(expected);

        List<Skill> actual = requestValidation.validateRequest(dto);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void validateRequestDtoIsNull() {

        assertThrows(IllegalArgumentException.class, () -> requestValidation.validateRequest(null));
    }


    @Test
    public void validateRequestMessageIsNull() {

        dto.setMessage(null);
        assertThrows(IllegalArgumentException.class, () -> requestValidation.validateRequest(dto));
    }

    @Test
    public void validateRequestMessageIsBlank() {

        dto.setMessage("    ");
        assertThrows(IllegalArgumentException.class, () -> requestValidation.validateRequest(dto));
    }

    @Test
    public void validateRequestCheckUserExistsById() {
        doThrow(new EntityNotFoundException()).when(userValidator).checkUserExistsById(anyLong());
        assertThrows(EntityNotFoundException.class, () -> requestValidation.validateRequest(dto));
    }

    @Test
    public void validateRequestCheckUserExistsById2() {
        doNothing().doThrow(new EntityNotFoundException()).when(userValidator).checkUserExistsById(anyLong());
        assertThrows(EntityNotFoundException.class, () -> requestValidation.validateRequest(dto));
    }

    @Test
    public void validateRequestByLatestRequest() {
        RecommendationRequest latestRecommendationRequest = new RecommendationRequest();
        latestRecommendationRequest.setCreatedAt(LocalDateTime.now().minusMonths(3));

        when(requestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(latestRecommendationRequest));

        assertThrows(IllegalStateException.class, () -> requestValidation.validateRequest(dto));
    }

    @Test
    public void validateRequestWithoutSkills() {
        dto.setSkillsIds(List.of());

        List<Skill> actual = requestValidation.validateRequest(dto);

        assertNotNull(actual);
        assertEquals(List.of(), actual);
    }

    @Test
    public void validateRequestSkillsNotFound() {
        when(skillRepository.findAllById(anyList())).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> requestValidation.validateRequest(dto));
    }
}