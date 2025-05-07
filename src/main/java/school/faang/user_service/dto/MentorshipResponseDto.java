package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorshipResponseDto {

    @JsonProperty("requesterId")
    private Long requester;

    @JsonProperty("receiverId")
    private Long receiver;

    @JsonProperty("description")
    private String description;



}
