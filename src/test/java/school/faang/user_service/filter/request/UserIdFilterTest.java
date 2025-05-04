package school.faang.user_service.filter.request;

import org.mockito.Mock;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserIdFilterTest {

    @Mock
    protected RecommendationRequest request1;
    @Mock
    protected RecommendationRequest request2;
    @Mock
    protected User user1;
    @Mock
    protected User user2;

    protected void testIsApplicableShouldReturnTrueWhenUserIdIsPresent(RecommendationRequestFilter filter) {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(321L);
        filterDto.setReceiverId(123L);

        assertTrue(filter.isApplicable(filterDto));
    }


    protected void testIsApplicableShouldReturnFalseWhenUserIdIsNull(RecommendationRequestFilter filter) {
        RequestFilterDto filterDto = new RequestFilterDto();

        assertFalse(filter.isApplicable(filterDto));
    }

    protected void testApplyShouldFilterRequestsByUserId(RecommendationRequestFilter filter, List<Supplier<User>> suppliers) {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(1L);

        filterDto.setRequesterId(1L);

        when(suppliers.get(0).get()).thenReturn(user1);
        when(suppliers.get(1).get()).thenReturn(user2);
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);

        Stream<RecommendationRequest> result = filter.apply(
                Stream.of(request1, request2),
                filterDto
        );

        List<RecommendationRequest> filtered = result.toList();
        assertEquals(1, filtered.size());
        assertTrue(filtered.contains(request1));
    }

    protected void testApplyWhenNoMatchesShouldReturnEmptyStream(RecommendationRequestFilter filter, Supplier<User> function) {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(3L);
        filterDto.setReceiverId(3L);

        when(function.get()).thenReturn(user1);
        when(user1.getId()).thenReturn(1L);

        Stream<RecommendationRequest> result = filter.apply(
                Stream.of(request1),
                filterDto
        );

        assertEquals(0, result.count());
    }

    protected void testApplyWhenEmptyStreamShouldReturnEmptyStream(RecommendationRequestFilter filter) {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(1L);
        filterDto.setReceiverId(1L);

        Stream<RecommendationRequest> result = filter.apply(
                Stream.empty(),
                filterDto
        );

        assertEquals(0, result.count());
    }
}