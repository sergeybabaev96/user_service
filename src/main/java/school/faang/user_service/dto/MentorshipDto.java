package school.faang.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class MentorshipDto {
    private String username;
    private Long id;
}
