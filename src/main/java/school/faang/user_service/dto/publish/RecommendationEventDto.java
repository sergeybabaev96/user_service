package school.faang.user_service.dto.publish;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEventDto {

    private long id;
    private Long receiverId;
    private Long requesterId;
    private LocalDateTime createdAt;

}
