package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class TestRecommendationController {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private RecommendationController recommendationController;

    private static RecommendationDto recommendationDtoValid;
    private static RecommendationDto recommendationDtoInvalid;
    private static Recommendation recommendationValid;
    @Spy
    private static SkillOfferDto skillOfferDto;
    private static long validId;
    private static long invalidId;

    @BeforeAll
    static void setUp() {
        recommendationDtoValid = new RecommendationDto(
                1L,
                1L,
                2L,
                "Knows SQL",
                new ArrayList<SkillOfferDto>(),
                LocalDateTime.now());

        recommendationDtoInvalid = new RecommendationDto(
                1L,
                1L,
                2L,
                "",
                null,
                LocalDateTime.now());

        skillOfferDto = new SkillOfferDto(0L, "SQL", 1L, 2L, 1L);
        recommendationValid = null;
        validId = 1;
        invalidId = -2;
    }

    @Test
    public void testGiveRecommendationDtoContentIsValid() {
        recommendationController.giveRecommendation(recommendationDtoValid);
        Mockito.verify(recommendationService, Mockito.times(1)).create(recommendationValid);
    }

    @Test
    public void testGiveRecommendationDtoContentIsNotValid() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationController.giveRecommendation(recommendationDtoInvalid));
    }

    @Test
    public void testUpdateRecommendationDtoContentIsValid() {
        recommendationController.updateRecommendation(recommendationDtoValid);
        Mockito.verify(recommendationService, Mockito.times(1)).update(recommendationValid);
    }

    @Test
    public void testUpdateRecommendationDtoContentIsNotValid() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationController.updateRecommendation(recommendationDtoInvalid));
    }

    @Test
    public void testDeleteRecommendationIdValid() {
        recommendationController.deleteRecommendation(validId);
        Mockito.verify(recommendationService, Mockito.times(1)).delete(validId);
    }

    @Test
    public void testDeleteRecommendationIdNotValid() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationController.deleteRecommendation(invalidId));
    }

    @Test
    public void testGetAllUserRecommendationsIdValid() {
        recommendationController.getAllUserRecommendations(validId);
        Mockito.verify(recommendationService, Mockito.times(1)).getAllUserRecommendations(validId);
    }

    @Test
    public void testGetAllUserRecommendationsIdNotValid() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationController.getAllUserRecommendations(invalidId));
    }

    @Test
    void testGetAllGivenRecommendationsIdValid() {
        recommendationController.getAllGivenRecommendations(validId);
        Mockito.verify(recommendationService, Mockito.times(1)).getAllGivenRecommendations(validId);
    }

    @Test
    void testGetAllGivenRecommendationsIdNotValid() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationController.getAllGivenRecommendations(invalidId));
    }
}
