package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestFilterRequester implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getRequesterId() != null
                && requestFilterDto.getRequesterId() != 0;
    }

    @Override
    public Stream<MentorshipRequest> filter(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return requests.filter(request ->
                filter.getRequesterId().equals(request.getRequester().getId()));
    }
}
