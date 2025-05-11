package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

@Component
public interface MentorshipValidator {

    void validate(MentorshipRequestDto dto);
}
