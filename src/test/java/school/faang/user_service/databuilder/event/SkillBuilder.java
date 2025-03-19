package school.faang.user_service.databuilder.event;

import lombok.experimental.UtilityClass;
import school.faang.user_service.entity.Skill;

@UtilityClass
public class SkillBuilder {
    public static Skill createValidSkill(Long id, String title) {
        return Skill.builder()
                .id(id)
                .title(title)
                .build();
    }
}
