package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@
class RequestValidationTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRequestRepository requestRepository;

    @Mock
    private UserValidator userValidator;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void validateRequest() {
        List<Skill> expected = new ArrayList<>();

        RequestValidation requestValidation = new RequestValidation(skillRepository, requestRepository, userValidator);

        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(3L);
        dto.setMessage("message");
        dto.setStatus(RequestStatus.ACCEPTED);
        dto.setSkillsIds(new ArrayList<>());
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setCreatedAt(LocalDateTime.now().minusDays(3));
        dto.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(skillRepository.findAllById(anyList())).thenReturn(expected);



        List<Skill> actual = requestValidation.validateRequest(dto);
        assertEquals(1, actual.size());
        assertEquals(expected, actual);
        assertThrows(IllegalArgumentException.class, () -> requestValidation.validateRequest(dto));
    }

    @Test
    void validateRequest1() {
    }
}