package school.faang.user_service.messages;

public class ErrorMessages {
    public static final String USER_NOT_FOUND_ERROR = "No user with ID %d\n";
    public static final String NO_ACTIVE_PREMIUM = "No active premium found for user with ID %d\n";
    public static final String UNABLE_TO_PAY_PREMIUM = "Unable to pay premium for user with ID %d";
    public static final String PREMIUM_HAS_ALREADY_BEEN_PURCHASED = "Premium has already been purchased for user with ID %d";
    public static final String NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE = "No response received from payment service when trying to get premium subscription price";
    public static final String NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE = "No response received from payment service when trying to pay premium subscription";
}
