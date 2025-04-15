package school.faang.user_service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.PremiumPaymentReplyNotReceivedException;
import school.faang.user_service.exception.PremiumPriceReplyNotReceivedException;

import static school.faang.user_service.messages.ErrorMessages.NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;
import static school.faang.user_service.messages.ErrorMessages.NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PremiumPriceReplyNotReceivedException.class)
    public ResponseEntity<ErrorResponse> handlePremiumReplyNotReceivedException(PremiumPriceReplyNotReceivedException exception) {
        String message = NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        HttpStatus.GATEWAY_TIMEOUT, exception.getMessage())
                .title("Something went wrong with payment service.")
                .detail(message)
                .property("service", "payment")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(PremiumPaymentReplyNotReceivedException.class)
    public ResponseEntity<ErrorResponse> handlePremiumPaymentReplyNotReceivedException(PremiumPaymentReplyNotReceivedException exception) {
        String message = NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;
        ErrorResponse errorResponse = ErrorResponse.builder(exception,
                        HttpStatus.GATEWAY_TIMEOUT, exception.getMessage())
                .title("Something went wrong with payment service.")
                .detail(message)
                .property("service", "payment")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
    }
}
