package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequestDto {

    private Long id;

    @NotNull(message = "Message can't be null")
    @NotBlank(message = "Message can't be blank")
    private String message;

    private String status;

    @NotNull(message = "Skills can't be null")
    private List<Long> skillsIds;

    @NotNull(message = "Requester id can't be null")
    @Min(value = 1, message = "Requester id should be more than 0")
    private Long requesterId;

    @NotNull(message = "Receiver id can't be null")
    @Min(value = 1, message = "Receiver id should be more than 0")
    private Long receiverId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
