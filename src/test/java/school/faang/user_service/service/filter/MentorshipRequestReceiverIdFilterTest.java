package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestReceiverIdFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestReceiverIdFilterTest {
    private final MentorshipRequestReceiverIdFilter mentorshipRequestReceiverIdFilter = new MentorshipRequestReceiverIdFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = mentorshipRequestReceiverIdFilter.isApplicable(new RequestFilterDto(null, null, 1L, null));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = mentorshipRequestReceiverIdFilter.isApplicable(new RequestFilterDto(null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply() {
        User receiver1 = User.builder().id(1L).build();
        User receiver2 = User.builder().id(2L).build();
        User receiver3 = User.builder().id(3L).build();

        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, null, null, receiver1, null, null, null, null),
                new MentorshipRequest(2L, null, null, receiver2, null, null, null, null),
                new MentorshipRequest(3L, null, null, receiver3, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestReceiverIdFilter.apply(requests, new RequestFilterDto(null, null, 1L, null));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(1, requestList.size());
        assertEquals(receiver1, requestList.get(0).getReceiver());
    }

    @Test
    public void testApplyNoSuitableRequests() {
        User receiver =  User.builder().id(2L).build();

        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, null, null, receiver, null, null, null, null),
                new MentorshipRequest(2L, null, null, receiver, null, null, null, null),
                new MentorshipRequest(3L, null, null, receiver, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestReceiverIdFilter.apply(requests, new RequestFilterDto(null, null, 1L, null));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(0, requestList.size());
    }
}
