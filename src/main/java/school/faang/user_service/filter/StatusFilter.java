package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public class StatusFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> request, RequestFilterDto filterRequestDto) {
        return request.filter(req -> req.getStatus().equals(filterRequestDto.getStatus()));
    }
}
