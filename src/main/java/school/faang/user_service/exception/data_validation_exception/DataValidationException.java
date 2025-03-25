package school.faang.user_service.exception.data_validation_exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }

    public static final String TIME_EXCEPTION = "time validation is failed";
    public static final String USER_DOESNT_EXIST = "user does not exist";
    public static final String WORK_SCHEDULE_DOESNT_EXIST = "work schedule does not exist";
    public static final String USER_DOESNT_HAVE_ACCESS = "you does not have access";

}
