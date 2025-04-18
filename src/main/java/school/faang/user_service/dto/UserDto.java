package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private boolean active;
    private String aboutMe;
    private Long countryId;
    private String city;
    private Integer experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> ownedEventsIds;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
    private List<Long> setGoalsIds;
    private List<Long> goalsIds;
}
