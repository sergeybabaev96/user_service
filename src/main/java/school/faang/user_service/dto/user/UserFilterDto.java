package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @Size(max = 64, message = "The length must not exceed 64 characters")
    private String namePattern;

    @Size(max = 255, message = "The length must not exceed 255 characters")
    private String aboutPattern;

    private String emailPattern;
    private String contactPattern;

    @Size(max = 64, message = "The length must not exceed 64 characters")
    private String countryPattern;

    @Size(max = 64, message = "The length must not exceed 64 characters")
    private String cityPattern;

    @Size(max = 15, message = "The length must not exceed 15 characters")
    private String phonePattern;

    @Size(max = 64, message = "The length must not exceed 64 characters")
    private String skillPattern;

    private int experienceMin = 0;
    private int experienceMax = Integer.MAX_VALUE;
    private int page = 0;
    private int pageSize = 10;
}
