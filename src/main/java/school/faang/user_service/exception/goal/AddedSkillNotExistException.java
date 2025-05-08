package school.faang.user_service.exception.goal;

public class AddedSkillNotExistException extends RuntimeException {

    public AddedSkillNotExistException() {
        super("Contains skill that not exist!!");
    }
}