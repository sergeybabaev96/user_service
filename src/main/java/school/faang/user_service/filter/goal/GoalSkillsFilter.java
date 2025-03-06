package school.faang.user_service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class GoalSkillsFilter implements GoalFilter {
    private final SkillRepository skillRepository;

    @Override
    public boolean isApplicable(SearchGoalDto searchGoalDto) {
        return Objects.nonNull(searchGoalDto.skillIds()) && !searchGoalDto.skillIds().isEmpty();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, SearchGoalDto searchGoalDto) {
        List<Skill> allSkillByIds = skillRepository.findAllById(searchGoalDto.skillIds());
        return goals.filter(goal -> !goal.getSkillsToAchieve().isEmpty()
                && new HashSet<>(goal.getSkillsToAchieve()).containsAll(allSkillByIds));
    }
}