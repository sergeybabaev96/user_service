package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Builder
public record GoalDto(@JsonProperty("title") @NotBlank String title,
                      @JsonProperty("description") String description,
                      @JsonProperty("parent") Long parent,
                      @JsonProperty("status") GoalStatus status,
                      @JsonProperty("skillIds") List<Long> skillIds,
                      @JsonProperty("mentorId") Long mentorId) {
}
