package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MentorshipRequestDto {

    @JsonProperty("requesterId")
    private long requesterId;

    @JsonProperty("receiverId")
    private long receiverId;

    @JsonProperty("description")
    private String description;

}