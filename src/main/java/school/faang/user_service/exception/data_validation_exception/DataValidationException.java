package school.faang.user_service.exception.data_validation_exception;

import lombok.Getter;

@Getter
public class DataValidationException extends RuntimeException {

    public static final String TIME_EXCEPTION = "time validation failed";
    public static final int TIME_EXCEPTION_CODE = 700;
    public static final String USER_DOESNT_EXIST = "user does not exist";
    public static final int USER_DOESNT_EXIST_CODE = 701;
    public static final String WORK_SCHEDULE_DOESNT_EXIST = "work schedule does not exist";
    public static final int WORK_SCHEDULE_DOESNT_EXIST_CODE = 702;
    public static final String USER_DOESNT_HAVE_ACCESS = "you does not have access";
    public static final int USER_DOESNT_HAVE_ACCESS_CODE = 703;
    private int exceptionCode;

    public DataValidationException(String message) {
        super(message);
    }
}
