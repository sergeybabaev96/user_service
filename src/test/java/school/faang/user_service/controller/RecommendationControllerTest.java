package school.faang.user_service.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void testGiveRecommendation() {
        RecommendationDto inputDto = new RecommendationDto();
        inputDto.setContent("Test recommendation");

        when(recommendationService.create(inputDto)).thenReturn(inputDto);

        RecommendationDto result = recommendationController.giveRecommendation(inputDto);

        assertNotNull(result);
        assertEquals(inputDto.getContent(), result.getContent());
        verify(recommendationService).create(inputDto);
    }

    @Test
    void testUpdateRecommendation() {
        RecommendationDto updateDto = new RecommendationDto();
        updateDto.setContent("Updated content");

        when(recommendationService.update(updateDto)).thenReturn(updateDto);

        RecommendationDto result = recommendationController.updateRecommendation(updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getContent(), result.getContent());
        verify(recommendationService).update(updateDto);
    }

    @Test
    void testDeleteRecommendation() {
        Long recommendationId = 1L;
        doNothing().when(recommendationService).delete(recommendationId);

        recommendationController.deleteRecommendation(recommendationId);

        verify(recommendationService).delete(recommendationId);
    }

    @Test
    void testGetAllUserRecommendations() {
        long receiverId = 1L;
        List<RecommendationDto> expectedRecommendations = List.of(new RecommendationDto());

        when(recommendationService.getAllUserRecommendations(receiverId)).thenReturn(expectedRecommendations);

        List<RecommendationDto> result = recommendationController.getAllUserRecommendations(receiverId);

        assertNotNull(result);
        assertEquals(expectedRecommendations.size(), result.size());
        verify(recommendationService).getAllUserRecommendations(receiverId);
    }

    @Test
    void testGetAllGivenRecommendations() {
        long authorId = 1L;
        List<RecommendationDto> expectedRecommendations = List.of(new RecommendationDto());

        when(subscriptionService.getAllGivenRecommendations(authorId)).thenReturn(expectedRecommendations);

        List<RecommendationDto> result = recommendationController.getAllGivenRecommendations(authorId);

        assertNotNull(result);
        assertEquals(expectedRecommendations.size(), result.size());
        verify(subscriptionService).getAllGivenRecommendations(authorId);
    }

}
