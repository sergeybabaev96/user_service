package school.faang.user_service.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorModel {

    private String message;

    private int statusCode;

    private String serviceName;

}