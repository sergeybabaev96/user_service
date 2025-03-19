package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class GoalSkillsFilterTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalSkillsFilter filter;

    private GoalFilterDto goalFilterDto;
    private Goal goal;

    @BeforeEach
    void setUp() {
        goalFilterDto = new GoalFilterDto();
        goalFilterDto.setSkillsToAchieve(List.of(1L, 2L, 3L));

        goal = new Goal();
        List<Skill> skills;
        skills = List.of(
                Skill.builder().title("discipline").build(),
                Skill.builder().title("communication").build(),
                Skill.builder().title("web knowledge").build()
        );
        goal.setSkillsToAchieve(skills);
    }

    @Test
    @DisplayName("Фильтр с пустыми навыками")
    void isApplicableEmptySkills() {
        goalFilterDto.setSkillsToAchieve(null);
        List<Long> goalIds = goalFilterDto.getSkillsToAchieve();
        Mockito.when(skillRepository.findAllById(goalIds)).thenReturn(new ArrayList<>());

        boolean result = filter.isApplicable(goalFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр с несуществующими навыками")
    void isApplicableNonExistentSkills() {
        List<Long> goalIds = goalFilterDto.getSkillsToAchieve();
        Mockito.when(skillRepository.findAllById(goalIds)).thenReturn(List.of(new Skill()));

        boolean result = filter.isApplicable(goalFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Фильтр с валидными данными")
    void isApplicableTrue() {
        List<Long> goalIds = goalFilterDto.getSkillsToAchieve();
        Mockito.when(skillRepository.findAllById(goalIds)).thenReturn(goal.getSkillsToAchieve());

        boolean result = filter.isApplicable(goalFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Успешное применение фильтра")
    void testApplyFilter() {
        List<Long> goalIds = goalFilterDto.getSkillsToAchieve();
        Mockito.when(skillRepository.findAllById(goalIds)).thenReturn(goal.getSkillsToAchieve());
        List<Skill> skillsFirst = List.of(
                Skill.builder().title("discipline").build(),
                Skill.builder().title("random").build(),
                Skill.builder().title("web knowledge").build()
        );
        List<Skill> skillsSecond = goal.getSkillsToAchieve();
        List<Skill> skillsThird = List.of(
                Skill.builder().title("nothing").build(),
                Skill.builder().title("learn doing a flip").build(),
                Skill.builder().title("singing").build()
        );
        Stream<Goal> goals = Stream.of(
                Goal.builder().skillsToAchieve(skillsFirst).build(),
                Goal.builder().skillsToAchieve(skillsSecond).build(),
                Goal.builder().skillsToAchieve(skillsThird).build());

        Stream<Goal> filteredGoals = filter.apply(goals, goalFilterDto);
        List<Goal> filteredGoalsList = filteredGoals.toList();

        assertEquals(1, filteredGoalsList.size());
    }

    @Test
    @DisplayName("Успешное применение фильтра к пустому списку")
    void testApplyFilterEmptyStream() {
        Stream<Goal> goals = Stream.of();

        Stream<Goal> filteredGoals = filter.apply(goals, goalFilterDto);
        List<Goal> filteredGoalsList = filteredGoals.toList();

        assertEquals(0, filteredGoalsList.size());
    }
}