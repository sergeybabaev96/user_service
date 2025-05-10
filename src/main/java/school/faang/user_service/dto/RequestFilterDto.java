package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private Long recommendationId;
    private String messagePattern;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}