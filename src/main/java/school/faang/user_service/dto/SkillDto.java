package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SkillDto {

    private Long id;

    @NotBlank(message = "title must not be null or empty")
    @Size(max = 64)
    private String title;
    private List<Long> userId;
}