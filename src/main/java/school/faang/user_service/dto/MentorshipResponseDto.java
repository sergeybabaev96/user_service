package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class MentorshipResponseDto {

    private int id;

    @JsonProperty("requesterId")
    private Long requester;

    @JsonProperty("receiverId")
    private Long receiver;

    private String status;

    private String description;

    private LocalDateTime createdAt;
}