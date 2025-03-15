package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    private int experienceMin = 0;
    @Builder.Default
    private int experienceMax = Integer.MAX_VALUE;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer pageSize = 10;
}
