package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationRequestStatusFilterTest {
    private final RecommendationRequestStatusFilter recommendationRequestStatusFilter =
            new RecommendationRequestStatusFilter();

    @Test
    public void testIsApplicableTtue() {
        boolean result = recommendationRequestStatusFilter.isApplicable(new RequestFilterDto(RequestStatus.ACCEPTED));
        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = recommendationRequestStatusFilter.isApplicable(new RequestFilterDto(null));
        assertFalse(result);
    }

    @Test
    public void testApplySuitableRecommendationRequest() {
        Stream<RecommendationRequest> stream = Stream.of(RecommendationRequest.builder()
                        .status(RequestStatus.ACCEPTED)
                        .build(),
                RecommendationRequest.builder()
                        .status(RequestStatus.PENDING)
                        .build());
        Stream<RecommendationRequest> stream1 = recommendationRequestStatusFilter
                .apply(stream, new RequestFilterDto(RequestStatus.ACCEPTED));

        List<RecommendationRequest> requestList = stream1.toList();
        assertEquals(1, requestList.size());
        assertEquals(RequestStatus.ACCEPTED, requestList.get(0).getStatus());
    }

    @Test
    public void testApplyNoSuitableRecommendationRequest() {
        Stream<RecommendationRequest> stream = Stream.of(RecommendationRequest.builder()
                        .status(RequestStatus.PENDING)
                        .build(),
                RecommendationRequest.builder()
                        .status(RequestStatus.PENDING)
                        .build());
        Stream<RecommendationRequest> stream1 = recommendationRequestStatusFilter
                .apply(stream, new RequestFilterDto(RequestStatus.ACCEPTED));

        List<RecommendationRequest> requestList = stream1.toList();
        assertEquals(0, requestList.size());

    }

}
