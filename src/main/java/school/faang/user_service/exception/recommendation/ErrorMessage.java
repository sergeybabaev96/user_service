package school.faang.user_service.exception.recommendation;

public class ErrorMessage {
    public static final String SKILL_NOT_EXIST = "SKILL WITH NAME %s DOES NOT EXIST IN SYSTEM";
    public static final String REQUEST_STATUS = "RECOMMENDATION REQUEST WITH ID %d HAS THE STATUS %s. " +
            "OPERATION CANNOT BE PERFORMED";
    public static final String SKILL_NOT_FOUND = "There is no skill with id = %d";
    public static final String REQUEST_NOT_FOUND = "Request with id %d not found";
}
