package school.faang.user_service.handler;

import feign.Response;
import feign.codec.ErrorDecoder;
import school.faang.user_service.exception.PremiumBadRequestException;
import school.faang.user_service.exception.PremiumNotFoundException;
import school.faang.user_service.exception.ServiceNotAvailableException;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new PremiumBadRequestException("Bad request");
            case 404 -> new PremiumNotFoundException("Endpoint not found");
            case 503 -> new ServiceNotAvailableException("Product Api is unavailable");
            case 500 -> new ServiceNotAvailableException("Internal server error in external service");
            case 502 -> new ServiceNotAvailableException("Bad gateway");
            default -> new ServiceNotAvailableException("Unexpected error: " + response.status());
        };
    }
}