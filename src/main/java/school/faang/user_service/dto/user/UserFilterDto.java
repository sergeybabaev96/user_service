package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private String usernamePattern;
    private String emailPattern;
    private String phonePattern;
    private String contactPattern;
    private Boolean active;
    private String aboutMePattern;
    private String countryPattern;
    private String cityPattern;
    private Integer experienceMoreThan;
    private LocalDateTime createdBefore;
    private String skillPattern;

    private int experienceMin = 0;
    private int experienceMax = Integer.MAX_VALUE;
    private Integer page = 0;
    private Integer pageSize = 10;
}
