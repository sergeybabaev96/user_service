package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MentorshipDto {
    Long id;
    @NotBlank
    String username;
    String email;
    String aboutMe;
    Integer experience;
    boolean active;
    List<Long> menteeIds;
    List<Long> mentorIds;
}
