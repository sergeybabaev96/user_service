package school.faang.user_service.dto;

import lombok.Data;

@Data
public class ErrorResponseDto {

    private final String errorMessage;

    public ErrorResponseDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}