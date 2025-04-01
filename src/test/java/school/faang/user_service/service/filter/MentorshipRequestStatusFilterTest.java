package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.MentorshipRequestStatusFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestStatusFilterTest {
    private final MentorshipRequestStatusFilter mentorshipRequestStatusFilter = new MentorshipRequestStatusFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = mentorshipRequestStatusFilter.isApplicable(new RequestFilterDto(null, null, null, RequestStatus.PENDING));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = mentorshipRequestStatusFilter.isApplicable(new RequestFilterDto(null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, null, null, null, RequestStatus.PENDING, null, null, null),
                new MentorshipRequest(2L, null, null, null, null, null, null, null),
                new MentorshipRequest(3L, null, null, null, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestStatusFilter.apply(requests, new RequestFilterDto(null, null, null, RequestStatus.PENDING));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(1, requestList.size());
        assertEquals(RequestStatus.PENDING, requestList.get(0).getStatus());
    }

    @Test
    public void testApplyNoSuitableRequests() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, null, null, null, null, null, null, null),
                new MentorshipRequest(2L, null, null, null, null, null, null, null),
                new MentorshipRequest(3L, null, null, null, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestStatusFilter.apply(requests, new RequestFilterDto(null, null, null, RequestStatus.PENDING));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(0, requestList.size());
    }
}
