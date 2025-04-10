package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private List<Long> mentorIds;
    private List<Long> menteeIds;

}