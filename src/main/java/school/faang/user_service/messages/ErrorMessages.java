package school.faang.user_service.messages;

public class ErrorMessages {
    public static final String USER_NOT_FOUND_ERROR = "No user with ID %d\n";
    public static final String NO_ACTIVE_PREMIUM = "No active premium found for user iwtwith ID %d\n";
    public static final String FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE = "Failed to acknowledge Kafka message";
    public static final String PREMIUM_PRICE_NOT_FOUND = "Premium price for user with ID %d not found";
    public static final String PREMIUM_PAYMENT_RESPONSE_NOT_FOUND = "Premium payment response for user with ID %d not found";
}
