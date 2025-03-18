package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipRequestFilterDescriptionTest {
    MentorshipRequestFilterDescription mentorshipRequestFilterDescription = new MentorshipRequestFilterDescription();

    @Test
    void isApplicableTrue() {
        assertTrue(mentorshipRequestFilterDescription
                .isApplicable(new RequestFilterDto("description", null, null, null)));
    }

    @Test
    void isApplicableFalseWithNull() {
        assertFalse(mentorshipRequestFilterDescription
                .isApplicable(new RequestFilterDto(null, null, null, null)));
    }

    @Test
    void isApplicableFalseWithEmptyDescription() {
        assertFalse(mentorshipRequestFilterDescription
                .isApplicable(new RequestFilterDto("   ", null, null, null)));
    }

    @Test
    void testAppliedFilter() {
        Stream<MentorshipRequest> requestStream = Stream.of(
                MentorshipRequest.builder().description("descript").build(),
                MentorshipRequest.builder().description("description").build(),
                MentorshipRequest.builder().description("description too").build(),
                MentorshipRequest.builder().description("another").build()
        );
        RequestFilterDto filter = new RequestFilterDto("description", null, null, null);
        List<MentorshipRequest> filtered = mentorshipRequestFilterDescription.filter(requestStream, filter).toList();
        assertTrue(filtered.get(0).getDescription().contains(filter.getDescription()));
        assertEquals(2, filtered.size());
    }

    @Test
    void testAppliedFilterIgnoreCase() {
        Stream<MentorshipRequest> requestStream = Stream.of(
                MentorshipRequest.builder().description("descript").build(),
                MentorshipRequest.builder().description("description").build(),
                MentorshipRequest.builder().description("description too").build(),
                MentorshipRequest.builder().description("another").build()
        );
        RequestFilterDto filter = new RequestFilterDto("descRIPtion", null, null, null);
        List<MentorshipRequest> filtered = mentorshipRequestFilterDescription.filter(requestStream, filter).toList();
        assertEquals(2, filtered.size());
    }

    @Test
    void testAppliedFilterWithEmptyResult() {
        Stream<MentorshipRequest> requestStream = Stream.of(
                MentorshipRequest.builder().description("descript").build(),
                MentorshipRequest.builder().description("description").build(),
                MentorshipRequest.builder().description("description too").build(),
                MentorshipRequest.builder().description("another").build()
        );
        RequestFilterDto filter = new RequestFilterDto("Any text", null, null, null);
        List<MentorshipRequest> filtered = mentorshipRequestFilterDescription.filter(requestStream, filter).toList();
        assertEquals(0, filtered.size());
    }
}