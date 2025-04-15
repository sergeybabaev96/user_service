package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.MentorshipRequestFilter;

import java.util.stream.Stream;

public class TestMentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return true;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto requestFilterDto) {
        return requests.filter(request -> request.getStatus().equals(RequestStatus.PENDING));
    }
}
