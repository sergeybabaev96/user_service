package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestFilterStatusTest {
    MentorshipRequestFilterStatus mentorshipRequestFilterStatus = new MentorshipRequestFilterStatus();

    @Test
    void isApplicableTrue() {
        assertTrue(mentorshipRequestFilterStatus
                .isApplicable(new RequestFilterDto(null, null, null, RequestStatus.PENDING)));
    }

    @Test
    void isApplicableFalseWithNull() {
        assertFalse(mentorshipRequestFilterStatus
                .isApplicable(new RequestFilterDto(null, null, null, null)));
    }

    @Test
    void testAppliedFilter() {
        Stream<MentorshipRequest> requestStream = Stream.of(
                MentorshipRequest.builder().status(RequestStatus.ACCEPTED).build(),
                MentorshipRequest.builder().status(RequestStatus.PENDING).build(),
                MentorshipRequest.builder().status(RequestStatus.REJECTED).build(),
                MentorshipRequest.builder().status(RequestStatus.PENDING).build()
        );
        RequestFilterDto filter = new RequestFilterDto(null, null, null, RequestStatus.PENDING);
        List<MentorshipRequest> filtered = mentorshipRequestFilterStatus.filter(requestStream, filter).toList();
        assertEquals(filter.getStatus(), filtered.get(0).getStatus());
        assertEquals(2, filtered.size());
    }

    @Test
    void testAppliedFilterWithEmptyResult() {
        Stream<MentorshipRequest> requestStream = Stream.of(
                MentorshipRequest.builder().status(RequestStatus.ACCEPTED).build(),
                MentorshipRequest.builder().status(RequestStatus.PENDING).build(),
                MentorshipRequest.builder().status(RequestStatus.ACCEPTED).build(),
                MentorshipRequest.builder().status(RequestStatus.PENDING).build()
        );
        RequestFilterDto filter = new RequestFilterDto(null, null, null, RequestStatus.REJECTED);
        List<MentorshipRequest> filtered = mentorshipRequestFilterStatus.filter(requestStream, filter).toList();
        assertEquals(0, filtered.size());
    }
}