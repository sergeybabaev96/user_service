package school.faang.user_service.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private String errorMsg;
    private LocalDateTime timestamp;
    private int codeResponse;
}