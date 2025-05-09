package school.faang.user_service.filter;

import school.faang.user_service.dto.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public class DescriptionFilter implements MentorshipFilter{
    @Override
    public boolean isApplicable(MentorshipFilterDto filterDto) {
        return filterDto.description() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, MentorshipFilterDto filterDto) {
        return mentorshipRequests
                .filter(request -> filterDto.description().contains(request.getDescription()));
    }
}
