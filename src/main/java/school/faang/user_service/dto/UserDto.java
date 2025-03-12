package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String email;
}
