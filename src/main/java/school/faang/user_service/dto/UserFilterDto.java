package school.faang.user_service.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    public static final String PHONE_NUMBER_REGEX = "^\\+?\\d+$";
    @Pattern(
            regexp = PHONE_NUMBER_REGEX,
            message = "Number must contain only numbers and may start with +"
    )
    private String namePattern;
    private String phonePattern;
    private int experienceMin;
    private int experienceMax;
}
