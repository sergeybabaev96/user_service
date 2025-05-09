package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.mentorship.MentorshipFilter;

import java.util.stream.Stream;

public class RequesterIdFilter implements MentorshipFilter {
    @Override
    public boolean isApplicable(MentorshipFilterDto filterDto) {
        return filterDto.requester() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, MentorshipFilterDto filterDto) {
        return mentorshipRequests
                .filter(request -> filterDto.requester().equals(request.getRequester()));
    }
}
