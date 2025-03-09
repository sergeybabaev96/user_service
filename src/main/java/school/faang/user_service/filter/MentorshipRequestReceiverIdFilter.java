package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public class MentorshipRequestReceiverIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return requests.filter(request -> request.getReceiver().getId().equals(filter.getReceiverId()));
    }
}
