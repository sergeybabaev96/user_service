package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserCreateDto {
    private String username;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
}
