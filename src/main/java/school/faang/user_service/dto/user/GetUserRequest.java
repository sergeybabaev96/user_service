package school.faang.user_service.dto.user;

import lombok.Data;
import school.faang.user_service.dto.UserDto;

@Data
public class GetUserRequest {
    private UserDto filter;
    private Integer limit;
    private Integer offset;
}
