package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private String status;

    @NotEmpty(message = "Skills cannot be empty")
    private List<String> skills;

    @NotNull(message = "Requester ID cannot be null")
    private Long requesterId;

    @NotNull(message = "Receiver ID cannot be null")
    private Long receiverId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}