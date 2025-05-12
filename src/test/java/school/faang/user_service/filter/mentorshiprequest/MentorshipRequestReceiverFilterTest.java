package school.faang.user_service.filter.mentorshiprequest;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestReceiverFilterTest {
    private final MentorshipRequestFilter filter = new MentorshipRequestReceiverFilter();

    @Test
    public void testRequestIsApplicable() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().receiverIdPattern(1L).build());
        assertTrue(result);
    }

    @Test
    public void testRequestIsNotApplicable() {
        boolean result = filter.isApplicable(RequestFilterDto.builder().build());
        assertFalse(result);
    }

    @Test
    public void testApplyRequestFilter() {
        Long pattern = 1L;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().receiver(User.builder().id(pattern).build()).build(),
                MentorshipRequest.builder().receiver(User.builder().id(10L).build()).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().receiverIdPattern(pattern).build())
                .toList();
        assertEquals(1, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getReceiver().getId());
    }

    @Test
    public void testApplySeveralApplicableRequests() {
        Long pattern = 1L;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().receiver(User.builder().id(pattern).build()).build(),
                MentorshipRequest.builder().receiver(User.builder().id(pattern).build()).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().receiverIdPattern(pattern).build())
                .toList();
        assertEquals(2, filteredStream.size());
        assertEquals(pattern, filteredStream.get(0).getReceiver().getId());
        assertEquals(pattern, filteredStream.get(1).getReceiver().getId());
    }

    @Test
    public void testApplyNoneApplicableRequests() {
        Long pattern = 1L;
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().receiver(User.builder().id(9L).build()).build(),
                MentorshipRequest.builder().receiver(User.builder().id(10L).build()).build()
        );
        List<MentorshipRequest> filteredStream = filter
                .apply(requests, RequestFilterDto.builder().receiverIdPattern(pattern).build())
                .toList();
        assertEquals(0, filteredStream.size());
    }
}