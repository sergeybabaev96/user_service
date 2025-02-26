package school.faang.user_service.controller.recommendation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.mapper.RecommendationEventMapper;
import school.faang.user_service.queue.RecommendationEventPublisher;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.time.LocalDateTime;
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

    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;
    @Mock
    private RecommendationEventMapper recommendationEventMapper;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void testGiveRecommendation() {
        RecommendationDto inputDto = new RecommendationDto();
        inputDto.setContent("Test recommendation");
        inputDto.setAuthorId(1L);
        inputDto.setReceiverId(2L);

        RecommendationDto createdDto = new RecommendationDto();
        createdDto.setId(1L);
        createdDto.setContent("Test recommendation");
        createdDto.setAuthorId(1L);
        createdDto.setReceiverId(2L);
        createdDto.setCreatedAt(LocalDateTime.now());

        RecommendationEvent event = new RecommendationEvent();
        event.setRecommendationId(1L);
        event.setAuthorId(1L);
        event.setReceiverId(2L);
        event.setCreatedAt(createdDto.getCreatedAt());

        when(recommendationService.create(inputDto)).thenReturn(createdDto);
        when(recommendationEventMapper.mapToRecommendationEvent(createdDto)).thenReturn(event);

        RecommendationDto result = recommendationController.giveRecommendation(inputDto);

        assertNotNull(result);
        assertEquals(createdDto.getId(), result.getId());
        assertEquals(createdDto.getContent(), result.getContent());
        assertEquals(createdDto.getAuthorId(), result.getAuthorId());
        assertEquals(createdDto.getReceiverId(), result.getReceiverId());
        assertEquals(createdDto.getCreatedAt(), result.getCreatedAt());

        verify(recommendationService, times(1)).create(inputDto);
        verify(recommendationEventMapper, times(1)).mapToRecommendationEvent(createdDto);
        verify(recommendationEventPublisher, times(1)).publish(event);
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
