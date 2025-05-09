package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @NotBlank
    private String namePattern;
    @NotBlank
    private String phonePattern;
    @NotNull
    private int experienceMin;
    @NotNull
    private int experienceMax;
}
