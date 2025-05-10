package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private String messagePattern;
    private String status;
}
