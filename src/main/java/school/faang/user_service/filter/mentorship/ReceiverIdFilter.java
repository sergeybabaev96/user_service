package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.mentorship.MentorshipFilter;

import java.util.stream.Stream;

public class ReceiverIdFilter implements MentorshipFilter {
    @Override
    public boolean isApplicable(MentorshipFilterDto filterDto) {
        return filterDto.receiver() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, MentorshipFilterDto filterDto) {
        return mentorshipRequests
                .filter(request -> filterDto.receiver().equals(request.getReceiver()));
    }
}
