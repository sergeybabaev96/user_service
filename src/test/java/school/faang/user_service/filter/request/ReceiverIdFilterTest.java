package school.faang.user_service.filter.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiverIdFilterTest {

    @InjectMocks
    private ReceiverIdFilter receiverIdFilter;

    @Mock
    private RecommendationRequest request1;
    @Mock
    private RecommendationRequest request2;
    @Mock
    private User user1;
    @Mock
    private User user2;

    @Test
    void isApplicableShouldReturnTrueWhenReceiverIdIsPresent() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(123L);

        assertTrue(receiverIdFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicableShouldReturnFalseWhenReceiverIdIsNull() {
        RequestFilterDto filterDto = new RequestFilterDto();

        assertFalse(receiverIdFilter.isApplicable(filterDto));
    }

    @Test
    void applyShouldFilterRequestsByReceiverId() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(1L);

        when(request1.getReceiver()).thenReturn(user1);
        when(request2.getReceiver()).thenReturn(user2);
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);

        Stream<RecommendationRequest> result = receiverIdFilter.apply(
                Stream.of(request1, request2),
                filterDto
        );

        List<RecommendationRequest> filtered = result.toList();
        assertEquals(1, filtered.size());
        assertTrue(filtered.contains(request1));
    }

    @Test
    void applyShouldReturnEmptyStreamWhenNoMatches() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(3L);

        when(request1.getReceiver()).thenReturn(user1);
        when(user1.getId()).thenReturn(1L);

        Stream<RecommendationRequest> result = receiverIdFilter.apply(
                Stream.of(request1),
                filterDto
        );

        assertEquals(0, result.count());
    }

    @Test
    void applyShouldHandleEmptyStream() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(1L);

        Stream<RecommendationRequest> result = receiverIdFilter.apply(
                Stream.empty(),
                filterDto
        );

        assertEquals(0, result.count());
    }

    @Test
    void applyShouldHandleMultipleMatchingRequests() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(1L);

        RecommendationRequest request3 = mock(RecommendationRequest.class);

        when(request1.getReceiver()).thenReturn(user1);
        when(request2.getReceiver()).thenReturn(user1);
        when(request3.getReceiver()).thenReturn(user2);
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);

        Stream<RecommendationRequest> result = receiverIdFilter.apply(
                Stream.of(request1, request2, request3),
                filterDto
        );

        assertEquals(2, result.count());
    }
}