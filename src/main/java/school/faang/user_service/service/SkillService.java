package school.faang.user_service.service;

import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    void updateAll(List<Skill> skills);

    Skill findById(Long id);

    List<Skill> findAllByIds(List<Long> ids);

    List<Skill> findAllByUserId(long userId);
}
