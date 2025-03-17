package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidationTest {
    @MockBean
    private SkillRepository skillRepository;

    @MockBean
    private RecommendationRequestRepository requestRepository;

    @MockBean
    private UserValidator userValidator;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void validateRequest() {
//        List<Skill> expected = new ArrayList<>();
//        RequestValidation requestValidation = new RequestValidation(skillRepository, requestRepository, userValidator);
//        RecommendationRequestDto dto = new RecommendationRequestDto();
//        List<Skill> actual = requestValidation.validateRequest(dto);
//        Assertions.assertEquals(1, actual.size());
//        Assertions.assertEquals(expected, actual);
////        Assertions.assertThrows(new IllegalArgumentException("Validation error: message is empty or null. DTO: " + dto), requestValidation.validateRequest(dto));
    }

    @Test
    void validateRequest1() {
    }
}