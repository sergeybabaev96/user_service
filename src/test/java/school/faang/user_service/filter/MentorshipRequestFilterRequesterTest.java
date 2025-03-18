package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorshipRequestFilterRequesterTest {
    MentorshipRequestFilterRequester mentorshipRequestFilterRequester = new MentorshipRequestFilterRequester();

    @Test
    void isApplicableTrue() {
        assertTrue(mentorshipRequestFilterRequester
                .isApplicable(new RequestFilterDto(null, 1L, null, null)));
    }

    @Test
    void isApplicableFalseWithNull() {
        assertFalse(mentorshipRequestFilterRequester
                .isApplicable(new RequestFilterDto(null, null, null, null)));
    }

    @Test
    void testAppliedFilter() {
        User first = User.builder().id(1L).build();
        User second = User.builder().id(2L).build();
        User third = User.builder().id(3L).build();
        Stream<MentorshipRequest> requestStream = Stream.of(
                MentorshipRequest.builder().receiver(second).requester(first).build(),
                MentorshipRequest.builder().receiver(third).requester(first).build(),
                MentorshipRequest.builder().receiver(first).requester(second).build(),
                MentorshipRequest.builder().receiver(third).requester(second).build()
        );
        RequestFilterDto filter = new RequestFilterDto(null, 1L, null, null);
        List<MentorshipRequest> filtered = mentorshipRequestFilterRequester.filter(requestStream, filter).toList();
        assertEquals(filter.getRequesterId(), filtered.get(0).getRequester().getId());
        assertEquals(2, filtered.size());
    }

    @Test
    void testAppliedFilterWithEmptyResult() {
        User first = User.builder().id(1L).build();
        User second = User.builder().id(2L).build();
        User third = User.builder().id(3L).build();
        Stream<MentorshipRequest> requestStream = Stream.of(
                MentorshipRequest.builder().receiver(second).requester(first).build(),
                MentorshipRequest.builder().receiver(third).requester(first).build(),
                MentorshipRequest.builder().receiver(first).requester(second).build(),
                MentorshipRequest.builder().receiver(third).requester(second).build()
        );
        RequestFilterDto filter = new RequestFilterDto(null, 4L, null, null);
        List<MentorshipRequest> filtered = mentorshipRequestFilterRequester.filter(requestStream, filter).toList();
        assertEquals(0, filtered.size());
    }
}