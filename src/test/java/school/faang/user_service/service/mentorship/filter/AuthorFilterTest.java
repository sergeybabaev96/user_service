package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.TestData;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;

import java.util.List;
import java.util.stream.Stream;

public class AuthorFilterTest {

    @Test
    public void testIsApplicableIfAuthorPatternIsAbsentThenReturnFalse() {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .descriptionPattern("description")
                .build();
        AuthorFilter authorFilter = new AuthorFilter();

        Assertions.assertFalse(authorFilter.isApplicable(filters));
    }

    @Test
    public void testIsApplicableIfAuthorPatternIsPresentThenReturnTrue() {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .authorPattern("author")
                .build();
        AuthorFilter authorFilter = new AuthorFilter();

        Assertions.assertTrue(authorFilter.isApplicable(filters));
    }

    @Test
    public void testApplySuccess() {
        AuthorFilter authorFilter = new AuthorFilter();
        Stream<MentorshipRequest> mentorshipRequestStream = TestData.getMentorshipRequestsStream();
        MentorshipRequestFilterDto filtersDto = MentorshipRequestFilterDto.builder()
                .authorPattern("Bob")
                .build();

        List<MentorshipRequest> result = authorFilter.apply(mentorshipRequestStream, filtersDto)
                .toList();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
    }
}
