package school.faang.user_service.filter.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiverIdFilterTest extends UserIdFilterTest {

    @InjectMocks
    private ReceiverIdFilter receiverIdFilter;

    @Test
    void testIsApplicableShouldReturnTrueWhenReceiverIdIsPresent() {
        testIsApplicableShouldReturnTrueWhenUserIdIsPresent(receiverIdFilter);
    }

    @Test
    void testIsApplicableShouldReturnFalseWhenReceiverIdIsNull() {
        testIsApplicableShouldReturnFalseWhenUserIdIsNull(receiverIdFilter);
    }

    @Test
    void testApplyShouldFilterRequestsByReceiverId() {
        testApplyShouldFilterRequestsByUserId(
                receiverIdFilter,
                List.of(
                        () -> request1.getReceiver(),
                        () -> request2.getReceiver()
                )
        );
    }

    @Test
    void testApplyWhenNoMatchesShouldReturnEmptyStream() {
        testApplyWhenNoMatchesShouldReturnEmptyStream(receiverIdFilter, () -> request1.getReceiver());
    }

    @Test
    void testApplyWhenEmptyStreamShouldReturnEmptyStream() {
        testApplyWhenEmptyStreamShouldReturnEmptyStream(receiverIdFilter);
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