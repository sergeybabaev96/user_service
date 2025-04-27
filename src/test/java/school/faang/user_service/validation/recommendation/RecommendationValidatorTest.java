package school.faang.user_service.validation.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    private Recommendation recommendation;
    private RecommendationCreateDto recommendationCreateDto;
    private SkillOfferCreateDto skillOfferCreateDto;
    private User author;
    private User receiver;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);

        receiver = new User();
        receiver.setId(2L);

        skillOfferCreateDto = new SkillOfferCreateDto();
        skillOfferCreateDto.setSkillId(1L);

        recommendationCreateDto = new RecommendationCreateDto();
        recommendationCreateDto.setAuthorId(author.getId());
        recommendationCreateDto.setReceiverId(receiver.getId());
        recommendationCreateDto.setSkillOffers(List.of(skillOfferCreateDto));

        recommendation = new Recommendation();
        LocalDateTime moreThanSixMonthsAgo = LocalDateTime.now().minusMonths(7);
        recommendation.setUpdatedAt(moreThanSixMonthsAgo);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        author.setRecommendationsGiven(List.of(recommendation));

        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
    }

    @DisplayName("Проверка успешной валидации рекомендации")
    @Test
    void validateRecommendationWithValidInputsTest() {
        Mockito.when(skillRepository.existsById(skillOfferCreateDto.getSkillId()))
                .thenReturn(true);

        Assertions.assertDoesNotThrow(() ->
                recommendationValidator.validate(recommendationCreateDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
        Mockito.verify(skillRepository, Mockito.times(1))
                .existsById(skillOfferCreateDto.getSkillId());
    }

    @DisplayName("Проверка получения ошибки при слишком раннем обновлении рекомендации")
    @Test
    void validateRecommendationTimeIntervalWithInvalidInputsTest() {
        LocalDateTime lessThanSixMonthsAgo = LocalDateTime.now().minusMonths(5);
        recommendation.setUpdatedAt(lessThanSixMonthsAgo);

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Updated recommendation too early", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }

    @DisplayName("Проверка получения ошибки при отсутствии даты обновления")
    @Test
    void validateRecommendationTimeIntervalWithoutInputsTest() {
        recommendation.setUpdatedAt(null);

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Update date is not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }

    @DisplayName("Проверка получения ошибки при отсутствии SkillOffers")
    @Test
    void validateRecommendationWithoutSkillOffersInputsTest() {
        recommendationCreateDto.setSkillOffers(null);

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Skill offers list is null", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }

    @DisplayName("Проверка получения ошибки при пустом списке SkillOffers")
    @Test
    void validateRecommendationWithEmptySkillOffersInputsTest() {
        recommendationCreateDto.setSkillOffers(List.of());

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Skill offer is Empty", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }

    @DisplayName("Проверка получения ошибки при отсутствии SkillOffer в репозитории")
    @Test
    void validateRecommendationWithoutSkillOffersEntityTest() {
        Mockito.when(skillRepository.existsById(skillOfferCreateDto.getSkillId()))
                .thenReturn(false);

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Skill offer not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }
}
