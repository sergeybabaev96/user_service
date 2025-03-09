package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return requests.filter(request -> request.getStatus() == filter.getStatus());
    }
}
