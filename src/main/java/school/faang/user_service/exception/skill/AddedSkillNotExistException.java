package school.faang.user_service.exception.skill;

public class AddedSkillNotExistException extends RuntimeException {

    public AddedSkillNotExistException(String skillsId) {
        super("Contains skills [%s] that not exist!!".formatted(skillsId));
    }
}