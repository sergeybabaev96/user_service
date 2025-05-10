package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class RequestFilterDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private String status;
    private Long skillId;
}
