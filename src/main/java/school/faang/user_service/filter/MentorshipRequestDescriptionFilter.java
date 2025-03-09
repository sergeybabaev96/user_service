package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public class MentorshipRequestDescriptionFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getDescription() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return requests.filter(request -> request.getDescription().contains(filter.getDescription()));
    }
}
