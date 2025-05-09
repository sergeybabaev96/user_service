package school.faang.user_service.validation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;

@Component
public interface MentorshipValidator {

    void validate(MentorshipRequestDto dto);
}
