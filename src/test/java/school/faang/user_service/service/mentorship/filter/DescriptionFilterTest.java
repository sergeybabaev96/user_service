package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.TestData;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;

import java.util.List;
import java.util.stream.Stream;

public class DescriptionFilterTest {

    @Test
    public void testIsApplicableIfDescriptionPatternIsAbsentThenReturnFalse() {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .authorPattern("author")
                .build();
        DescriptionFilter filter = new DescriptionFilter();

        Assertions.assertFalse(filter.isApplicable(filters));
    }

    @Test
    public void testIsApplicableIfDescriptionPatternIsPresentThenReturnTrue() {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .descriptionPattern("description")
                .build();
        DescriptionFilter filter = new DescriptionFilter();

        Assertions.assertTrue(filter.isApplicable(filters));
    }

    @Test
    public void testApplySuccess() {
        DescriptionFilter filter = new DescriptionFilter();
        Stream<MentorshipRequest> mentorshipRequestStream = TestData.getMentorshipRequestsStream();
        MentorshipRequestFilterDto filtersDto = MentorshipRequestFilterDto.builder()
                .descriptionPattern("descri")
                .build();

        List<MentorshipRequest> result = filter.apply(mentorshipRequestStream, filtersDto)
                .toList();

        Assertions.assertEquals(4, result.size());
    }
}
