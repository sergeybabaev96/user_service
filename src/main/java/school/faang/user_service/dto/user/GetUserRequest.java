package school.faang.user_service.dto.user;

import lombok.Data;

@Data
public class GetUserRequest {
    private UserDto filter;
    private Integer limit;
    private Integer offset;
}
