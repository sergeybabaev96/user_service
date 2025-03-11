package school.faang.user_service.dto.skill;

import lombok.Builder;

@Builder
public record ResponseSkillDto(

        Long id,

        String title) {
}
