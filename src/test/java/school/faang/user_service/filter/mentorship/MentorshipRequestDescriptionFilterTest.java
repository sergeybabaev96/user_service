package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestDescriptionFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestDescriptionFilterTest {
    private final MentorshipRequestDescriptionFilter mentorshipRequestDescriptionFilter = new MentorshipRequestDescriptionFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = mentorshipRequestDescriptionFilter.isApplicable(new RequestFilterDto("description", null, null, null));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalseWhenDescriptionIsEmpty() {
        boolean result = mentorshipRequestDescriptionFilter.isApplicable(new RequestFilterDto("", null, null, null));

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalseWhenDescriptionIsBlank() {
        boolean result = mentorshipRequestDescriptionFilter.isApplicable(new RequestFilterDto("    ", null, null, null));

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = mentorshipRequestDescriptionFilter.isApplicable(new RequestFilterDto(null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "description", null, null, null, null, null, null),
                new MentorshipRequest(2L, "", null, null, null, null, null, null),
                new MentorshipRequest(3L, "", null, null, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestDescriptionFilter.apply(requests, new RequestFilterDto("description", null, null, null));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(1, requestList.size());
        assertEquals("description", requestList.get(0).getDescription());
    }

    @Test
    public void testApplyIgnoreCase() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "description", null, null, null, null, null, null),
                new MentorshipRequest(2L, "dEsCrIpTiOn", null, null, null, null, null, null),
                new MentorshipRequest(3L, "", null, null, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestDescriptionFilter.apply(requests, new RequestFilterDto("description", null, null, null));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(2, requestList.size());
        assertEquals("description", requestList.get(0).getDescription().toLowerCase());
        assertEquals("description", requestList.get(1).getDescription().toLowerCase());
    }

    @Test
    public void testApplyNoSuitableRequests() {
        Stream<MentorshipRequest> requests = Stream.of(
                new MentorshipRequest(1L, "", null, null, null, null, null, null),
                new MentorshipRequest(2L, "", null, null, null, null, null, null),
                new MentorshipRequest(3L, "", null, null, null, null, null, null)
        );
        Stream<MentorshipRequest> request = mentorshipRequestDescriptionFilter.apply(requests, new RequestFilterDto("description", null, null, null));

        List<MentorshipRequest> requestList = request.toList();
        assertEquals(0, requestList.size());
    }
}
