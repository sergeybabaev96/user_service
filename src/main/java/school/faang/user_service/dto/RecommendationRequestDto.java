package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDto {
    @NotBlank(message = "Message cannot be empty")
    private String message;
    private List<Long> skillIds;
    private Long requesterId;
    private Long receiverId;
}