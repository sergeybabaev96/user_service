package school.faang.user_service.exception;

import lombok.Getter;

@Getter
public class DataValidationException extends RuntimeException{

    public static final String TIME_EXCEPTION = "time validation failed";
    public static final int TIME_EXCEPTION_CODE = 700;
    public static final String USER_DOESNT_EXIST = "user does not exist";
    public static final int USER_DOESNT_EXIST_CODE = 701;
    private int exceptionCode;

    public DataValidationException(String message, int exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
}
