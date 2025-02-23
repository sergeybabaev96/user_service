package school.faang.user_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RequestValidation;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestValidationTest {

    @InjectMocks
    private RequestValidation requestValidation;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRequestRepository requestRepository;

    private static final Long REQUESTER = 1L;
    private static final Long RECEIVER = 2L;
    private static final Long INVALID_DATA1 = 3L;
    private static final Long INVALID_DATA2= 4L;
    private RecommendationRequestDto recommendationRequestDto;
    private User requester;
    private User receiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requester = User.builder().id(REQUESTER).username("Requester").build();
        receiver = User.builder().id(RECEIVER).username("Receiver").build();

        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setId(REQUESTER);
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setStatus(RequestStatus.PENDING);
        recommendationRequestDto.setSkillsIds(Arrays.asList(REQUESTER, RECEIVER));
        recommendationRequestDto.setRequesterId(requester.getId());
        recommendationRequestDto.setReceiverId(receiver.getId());
        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
        recommendationRequestDto.setUpdatedAt(LocalDateTime.now());
    }

    private List<Skill> createSkills() {
        return Arrays.asList(
                Skill.builder().id(REQUESTER).title("Skill 1").build(),
                Skill.builder().id(RECEIVER).title("Skill 2").build()
        );
    }

    @Test
    void testValidateRequestWhenDtoIsNull() {
        BusinessException exception = assertThrows(BusinessException.class, () ->
                requestValidation.validateRequest(null)
        );

        assertEquals("Запрос не может быть Null", exception.getMessage());
    }

    @Test
    void testValidateRequestWhenMessageIsNullOrBlank() {
        recommendationRequestDto.setMessage(null);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                requestValidation.validateRequest(recommendationRequestDto)
        );

        assertTrue(exception.getMessage().contains("Ошибка проверки: сообщение пустое или null"));

        recommendationRequestDto.setMessage("   ");
        exception = assertThrows(BusinessException.class, () ->
                requestValidation.validateRequest(recommendationRequestDto)
        );

        assertTrue(exception.getMessage().contains("Ошибка проверки: сообщение пустое или null"));
    }

    @Test
    void testValidateRequestWhenUserDoesNotExist() {
        when(userRepository.existsById(REQUESTER)).thenReturn(false);
        when(userRepository.existsById(RECEIVER)).thenReturn(true);

        assertThrows(BusinessException.class, () ->
                requestValidation.validateRequest(recommendationRequestDto)
        );

        verify(userRepository).existsById(recommendationRequestDto.getRequesterId());
        verify(userRepository, times(0)).existsById(recommendationRequestDto.getReceiverId());
    }

    @Test
    void testValidateRequestWhenOneSkillIsMissing() {
        recommendationRequestDto.setSkillsIds(Arrays.asList(REQUESTER, RECEIVER, INVALID_DATA1));
        when(skillRepository.findAllById(Arrays.asList(REQUESTER, RECEIVER, INVALID_DATA1))).thenReturn(createSkills());
        assertThrows(BusinessException.class, () -> requestValidation.validateRequest(recommendationRequestDto));
    }

    @Test
    void testValidateRequestWhenTwoSkillsAreMissing() {
        recommendationRequestDto.setSkillsIds(Arrays.asList(REQUESTER, INVALID_DATA1, INVALID_DATA2));
        when(skillRepository.findAllById(Arrays.asList(REQUESTER, INVALID_DATA1, INVALID_DATA2))).thenReturn(Arrays.asList(Skill.builder().id(REQUESTER).build()));
        assertThrows(BusinessException.class, () -> requestValidation.validateRequest(recommendationRequestDto));
    }

    @Test
    void testValidateRequestSuccess() {
        when(userRepository.existsById(REQUESTER)).thenReturn(true);
        when(userRepository.existsById(RECEIVER)).thenReturn(true);
        when(skillRepository.findAllById(recommendationRequestDto.getSkillsIds())).thenReturn(createSkills());
        when(requestRepository.findLatestPendingRequest(REQUESTER, RECEIVER)).thenReturn(Optional.empty());

        List<Skill> result = requestValidation.validateRequest(recommendationRequestDto);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testValidateRequestWithNullSkills() {
        recommendationRequestDto.setSkillsIds(null);

        when(userRepository.existsById(REQUESTER)).thenReturn(true);
        when(userRepository.existsById(RECEIVER)).thenReturn(true);
        when(requestRepository.findLatestPendingRequest(REQUESTER, RECEIVER)).thenReturn(Optional.empty());

        List<Skill> result = requestValidation.validateRequest(recommendationRequestDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateRequestWhenRequestExistsSixMonths() {
        RecommendationRequest existingRequest = RecommendationRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .createdAt(LocalDateTime.now().minusMonths(6))
                .build();

        when(requestRepository.findLatestPendingRequest(requester.getId(), receiver.getId())).thenReturn(Optional.of(existingRequest));
        when(userRepository.existsById(REQUESTER)).thenReturn(true);
        when(userRepository.existsById(RECEIVER)).thenReturn(true);

        assertThrows(BusinessException.class, () ->
                requestValidation.validateRequest(recommendationRequestDto)
        );
        verify(requestRepository).findLatestPendingRequest(requester.getId(), receiver.getId());
    }
}