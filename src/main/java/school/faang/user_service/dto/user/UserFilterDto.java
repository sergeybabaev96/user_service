package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для фильтрации пользователей.
 * <p>
 * Содержит параметры для поиска и фильтрации списка пользователей.
 * </p>
 */
@Data
@NoArgsConstructor
public class UserFilterDto {
    @Size(min = 2, max = 50)
    private String namePattern;

    @Pattern(regexp = "\\+?[0-9]{7,15}")
    private String phonePattern;

    @Min(0)
    private int experienceMin;

    @Min(0)
    private int experienceMax;
}
