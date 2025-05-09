package school.faang.user_service.entity.goal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateGoalDto(
        Long id,
        @NotBlank(message = "Empty goal title not allowed!") String title,
        String description,
        Long parent,
        List<Long> skillsId
) {}