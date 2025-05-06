package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MentorshipRequestDto {

    @JsonProperty("requesterId")
    private Long requester;

    @JsonProperty("receiverId")
    private Long receiver;

    @JsonProperty("description")
    private String description;
}