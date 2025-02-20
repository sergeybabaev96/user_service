package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private final String lineLengthExceeded = "Длина строки не должна превышать 255 символов";

    @Size(max = 255, message = lineLengthExceeded )
    private String namePattern;
    @Size(max = 255, message = lineLengthExceeded )
    private String aboutPattern;
    @Size(max = 255, message = lineLengthExceeded )
    private String emailPattern;
    @Size(max = 255, message = lineLengthExceeded )
    private String contactPattern;
    @Size(max = 255, message = lineLengthExceeded )
    private String countryPattern;
    @Size(max = 255, message = lineLengthExceeded )
    private String cityPattern;
    @Size(max = 255, message = lineLengthExceeded )
    private String phonePattern;
    @Size(max = 255, message = lineLengthExceeded )
    private String skillPattern;
    private Integer experienceMin;
    private Integer experienceMax;
    private int page = 1;
    private int pageSize = 10;
}
