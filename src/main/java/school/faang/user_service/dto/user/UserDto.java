package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

import java.util.List;

@Data
@Builder
public class UserDto {
    private Long userId;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private List<MentorshipRequestDto> sentMentorshipRequests;
}
