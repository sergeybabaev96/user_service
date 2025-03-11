package school.faang.user_service.service.mentorship.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements RequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filters) {
        return filters.descriptionPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters) {
        return requests.filter(request -> request.getDescription().toLowerCase()
                .contains(filters.descriptionPattern().toLowerCase()));
    }
}
