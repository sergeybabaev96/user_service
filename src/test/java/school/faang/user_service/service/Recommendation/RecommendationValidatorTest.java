package school.faang.user_service.service.Recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Test
    public void testValidateRecommendationDate() {
        LocalDateTime thresholdDate = LocalDateTime.of(2024, 8, 9, 15, 15, 15);
        LocalDateTime recommendationDate = LocalDateTime.of(2024, 9, 2, 15, 15, 15);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationDate(recommendationDate, thresholdDate));
    }

    @Test
    public void testExceptionAuthorSkillGuaranteed() {
        Long skillId = 1L;
        Long guarantorId = 1L;
        Long userId = 2L;
        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .id(1L)
                .build();
        when(userSkillGuaranteeRepository.findBySkillIdAndGuarantorIdAndUserId(skillId, guarantorId, userId)).thenReturn(Optional.ofNullable(userSkillGuarantee));
        assertThrows(DataValidationException.class, () -> recommendationValidator.validatorExistenceUserSkillGuarantee(prepareDataRecommendationDto()));
    }

    private RecommendationDto prepareDataRecommendationDto() {
        return RecommendationDto.builder()
                .id(1L)
                .skillId(1L)
                .authorId(1L)
                .receiverId(2L)
                .build();
    }
}