package school.faang.user_service.service;

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
import school.faang.user_service.validation.RecommendationValidator;

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
    }

    @DisplayName("Позитивный тест метода validate")
    @Test
    void validateRecommendationWithValidInputsTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        Mockito.when(skillRepository.existsById(skillOfferCreateDto.getSkillId()))
                .thenReturn(true);

        Assertions.assertDoesNotThrow(() ->
            recommendationValidator.validate(recommendationCreateDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
        Mockito.verify(skillRepository, Mockito.times(1))
                .existsById(skillOfferCreateDto.getSkillId());
    }

    @DisplayName("Негативный тест метода validate с неправильными данными" +
            " в методе validateRecommendationTimeInterval")
    @Test
    void validateRecommendationTimeIntervalWithInvalidInputsTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));

        LocalDateTime lessThanSixMonthsAgo = LocalDateTime.now().minusMonths(5);
        recommendation.setUpdatedAt(lessThanSixMonthsAgo);

        Exception exception =  Assertions.assertThrows(DataValidationException.class, () ->
            recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Updated recommendation too early", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }
    @DisplayName("Негативный тест метода validate с отсутствием updateAt " +
            "в методе validateRecommendationTimeInterval")
    @Test
    void validateRecommendationTimeIntervalWithoutInputsTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        recommendation.setUpdatedAt(null);

        Exception exception =  Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Update date is not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }

    @DisplayName("Негативный тест на отсутствие SkillOffers")
    @Test
    void validateRecommendationWithoutSkillOffersInputsTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));

        recommendationCreateDto.setSkillOffers(null);

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
            recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Skill offers list is null", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }

    @DisplayName("Негативный тест на пустой SkillOffers")
    @Test
    void validateRecommendationWithEmptySkillOffersInputsTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));

        recommendationCreateDto.setSkillOffers(List.of());

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Skill offer is Empty", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }

    @DisplayName("Негативный тест на отсутсвие нужного SkillOffers в skillRepository")
    @Test
    void validateRecommendationWithoutSkillOffersEntityTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        Mockito.when(skillRepository.existsById(skillOfferCreateDto.getSkillId()))
                .thenReturn(false);

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
                recommendationValidator.validate(recommendationCreateDto));
        Assertions.assertEquals("Skill offer not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(author.getId());
    }
}
