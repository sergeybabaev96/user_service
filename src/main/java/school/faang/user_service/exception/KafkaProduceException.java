package school.faang.user_service.exception;

public class KafkaProduceException extends RuntimeException {

    public KafkaProduceException(String message) {
        super(message);
    }
}