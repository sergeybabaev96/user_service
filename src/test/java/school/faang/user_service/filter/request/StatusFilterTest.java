package school.faang.user_service.filter.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusFilterTest {

    @InjectMocks
    private StatusFilter statusFilter;

    @Mock
    private RecommendationRequest request1;
    @Mock
    private RecommendationRequest request2;

    @Test
    void testIsApplicableWhenStatusExistsShouldReturnTrue() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.PENDING);

        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenStatusNotExistsShouldReturnFalse() {
        RequestFilterDto filterDto = new RequestFilterDto();

        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testApplyShouldFilterRequestsByStatus() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.ACCEPTED);

        when(request1.getStatus()).thenReturn(RequestStatus.ACCEPTED);
        when(request2.getStatus()).thenReturn(RequestStatus.REJECTED);

        Stream<RecommendationRequest> result = statusFilter.apply(
                Stream.of(request1, request2),
                filterDto
        );

        List<RecommendationRequest> filtered = result.toList();
        assertEquals(1, filtered.size());
        assertSame(request1, filtered.get(0));
    }

    @Test
    void testApplyWhenNoMatchesShouldReturnEmptyStream() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.PENDING);

        when(request1.getStatus()).thenReturn(RequestStatus.ACCEPTED);

        Stream<RecommendationRequest> result = statusFilter.apply(
                Stream.of(request1),
                filterDto
        );

        assertEquals(0, result.count());
    }

    @Test
    void testApplyWhenEmptyStreamShouldReturnEmptyStream() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setStatus(RequestStatus.PENDING);

        Stream<RecommendationRequest> result = statusFilter.apply(
                Stream.empty(),
                filterDto
        );

        assertEquals(0, result.count());
    }
}