package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.TestData;

import java.util.List;
import java.util.stream.Stream;

public class StatusFilterTest {

    @Test
    public void testIsApplicableIfStatusPatternIsAbsentThenReturnFalse() {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .authorPattern("author")
                .build();
        StatusFilter filter = new StatusFilter();

        Assertions.assertFalse(filter.isApplicable(filters));
    }

    @Test
    public void testIsApplicableIfStatusPatternIsPresentThenReturnTrue() {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .statusPattern("PENDING")
                .build();
        StatusFilter filter = new StatusFilter();

        Assertions.assertTrue(filter.isApplicable(filters));
    }

    @Test
    public void testApplySuccess() {
        StatusFilter filter = new StatusFilter();
        Stream<MentorshipRequest> mentorshipRequestStream = TestData.getMentorshipRequestsStream();
        MentorshipRequestFilterDto filtersDto = MentorshipRequestFilterDto.builder()
                .statusPattern("PENDING")
                .build();

        List<MentorshipRequest> result = filter.apply(mentorshipRequestStream, filtersDto)
                .toList();

        Assertions.assertEquals(1, result.size());
    }
}
