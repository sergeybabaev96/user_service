package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;
@Data
public class UserDto {
    private Long id;
    private String username;
    private List<Long> mentorIds;
    private List<Long> menteeIds;
}