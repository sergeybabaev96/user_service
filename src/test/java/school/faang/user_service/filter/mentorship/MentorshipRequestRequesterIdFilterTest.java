package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestRequesterIdFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestRequesterIdFilterTest {
    private final MentorshipRequestRequesterIdFilter mentorshipRequestRequesterIdFilter = new MentorshipRequestRequesterIdFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = mentorshipRequestRequesterIdFilter.isApplicable(new RequestFilterDto(null, 1L, null, null));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = mentorshipRequestRequesterIdFilter.isApplicable(new RequestFilterDto(null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply() {
        User requester1 = User.builder().id(1L).build();
        User requester2 = User.builder().id(2L).build();
        User requester3 = User.builder().id(3L).build();

        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, null, requester1, null, null, null, null, null),
                new MentorshipRequest(2L, null, requester2, null, null, null, null, null),
                new MentorshipRequest(3L, null, requester3, null, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestRequesterIdFilter.apply(requests, new RequestFilterDto(null, 1L, null, null));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(1, requestList.size());
        assertEquals(requester1, requestList.get(0).getRequester());
    }

    @Test
    public void testApplyNoSuitableRequests() {
        User requester = User.builder().id(2L).build();

        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, null, requester, null, null, null, null, null),
                new MentorshipRequest(2L, null, requester, null, null, null, null, null),
                new MentorshipRequest(3L, null, requester, null, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestRequesterIdFilter.apply(requests, new RequestFilterDto(null, 1L, null, null));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(0, requestList.size());
    }
}
