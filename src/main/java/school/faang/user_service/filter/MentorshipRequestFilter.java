package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto filter);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter);
}
