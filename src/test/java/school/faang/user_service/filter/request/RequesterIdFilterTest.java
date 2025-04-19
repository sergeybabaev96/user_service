package school.faang.user_service.filter.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequesterIdFilterTest {

    @InjectMocks
    private RequesterIdFilter requesterIdFilter;

    @Mock
    private RecommendationRequest request1;
    @Mock
    private RecommendationRequest request2;
    @Mock
    private User user1;
    @Mock
    private User user2;

    @Test
    void testIsApplicableWhenRequesterIdExistsShouldReturnTrue() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(123L);

        assertTrue(requesterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenRequesterIdNotExistsShouldReturnFalse() {
        RequestFilterDto filterDto = new RequestFilterDto();

        assertFalse(requesterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testApplyShouldFilterRequestsByRequesterId() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(1L);

        when(request1.getRequester()).thenReturn(user1);
        when(request2.getRequester()).thenReturn(user2);
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);

        Stream<RecommendationRequest> result = requesterIdFilter.apply(
                Stream.of(request1, request2),
                filterDto
        );

        List<RecommendationRequest> filteredList = result.toList();
        assertEquals(1, filteredList.size());
        assertTrue(filteredList.contains(request1));
    }

    @Test
    void testApplyWhenNoMatchesShouldReturnEmptyStream() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(3L);

        when(request1.getRequester()).thenReturn(user1);
        when(user1.getId()).thenReturn(1L);

        Stream<RecommendationRequest> result = requesterIdFilter.apply(
                Stream.of(request1),
                filterDto
        );

        assertEquals(0, result.count());
    }

    @Test
    void testApplyWhenEmptyStreamShouldReturnEmptyStream() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(1L);

        Stream<RecommendationRequest> result = requesterIdFilter.apply(
                Stream.empty(),
                filterDto
        );

        assertEquals(0, result.count());
    }
}