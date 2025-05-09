package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MentorshipRequestDto {

    @JsonProperty("requesterId")
    @NotNull(message = "requesterId must not be null")
    private Long requester;

    @JsonProperty("receiverId")
    @NotNull(message = "receiverId must not be null")
    private Long receiver;

    @JsonProperty("description")
    @NotNull(message = "description must not be null")
    private String description;
}