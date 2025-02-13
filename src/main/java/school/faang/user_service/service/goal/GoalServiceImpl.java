package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.GoalFilterDto;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<Filter<Goal, GoalFilterDto>> goalFilters;
//    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Override
    public GoalDto createGoal(long userId, GoalDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found", userId)));
        checkActiveGoalsForUser(user);
        checkExistsSkills(dto.skillIds());
        Goal goal = goalRepository.create(dto.title(), dto.description(), dto.parent());
        goal.setParent(getParentGoal(dto));
        goal.setMentor(getMentorGoal(dto));
        updateSkillsInGoal(dto.skillIds(), goal);
        goalRepository.assignGoalToUser(goal.getId(), user.getId());
        return goalMapper.toDto(goal);
    }

    @Transactional
    @Override
    public GoalDto updateGoal(long id, GoalDto dto) {
        Goal goal = goalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Goal with id = %s not found", id)));
        checkCompletedStatus(dto, goal);
        checkExistsSkills(dto.skillIds());
        List<Skill> inputSkills = skillRepository.findAllById(dto.skillIds());
        if (GoalStatus.COMPLETED.equals(dto.status())) {
            for (User user : goal.getUsers()) {
                updateUserSkills(user, inputSkills);
            }
        }
        Goal updateGoal = goalMapper.update(dto, goal);
        updateGoal.setParent(getParentGoal(dto));
        updateGoal.setMentor(getMentorGoal(dto));
        goalRepository.assignGoalToUser(goal.getId(), dto.mentorId());
        Goal updatedGoal = goalRepository.save(updateGoal);
        return goalMapper.toDto(updatedGoal);
    }

    // send notification and analytics
    // kafkaTemplate.send();

    @Override
    public void deleteGoalById(long id) {
        List<Goal> subtasks = goalRepository.findByParent(id);
        if (!subtasks.isEmpty()) {
            subtasks.forEach(goalRepository::delete);
        }
        goalRepository.deleteById(id);
    }

    @Override
    public List<GoalDto> findSubgoalsByGoalId(long id, GoalFilterDto inputFilters) {
        Goal goal = goalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Goal with id = %d not found", id)));
        List<Goal> subtasks = goalRepository.findByParent(goal.getId());
        subtasks = filteredGoals(subtasks, inputFilters);
        return subtasks.stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Override
    public List<GoalDto> findGoalsByUser(long userId, GoalFilterDto inputFilters) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId);
        goals = filteredGoals(goals, inputFilters);
        return goals.stream()
                .map(goalMapper::toDto)
                .toList();
    }

    private void checkCompletedStatus(GoalDto dto, Goal goal) {
        if (GoalStatus.COMPLETED.equals(dto.status()) && GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new IllegalArgumentException(
                    String.format("Goal with id = %d already completed", goal.getId()));
        }
    }

    private void updateUserSkills(User user, List<Skill> inputSkills) {
        List<Skill> skills = user.getSkills();
        skills.addAll(inputSkills);
        user.setSkills(skills);
        userRepository.save(user);
    }

    private void updateSkillsInGoal(List<Long> skillIds, Goal goal) {
        skillIds.forEach(skillId -> {
            Skill skill = skillRepository.findById(skillId).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Skill with id = %d not found", skillId)));
            goal.getSkillsToAchieve().add(skill);
            goalRepository.save(goal);
        });
    }

    private void checkActiveGoalsForUser(User user) {
        long countActiveGoals = user.getGoals().stream()
                .filter(goal -> GoalStatus.ACTIVE.equals(goal.getStatus()))
                .count();
        if (countActiveGoals >= 3) {
            log.error("User with id = {} already has exists max count active goals", user.getId());
            throw new IllegalArgumentException(
                    String.format("User with id = %d already has exists max count active goals", user.getId()));
        }
    }

    private void checkExistsSkills(List<Long> skillIds) {
        skillIds.forEach(id -> {
            if (!skillRepository.existsById(id)) {
                throw new EntityNotFoundException(
                        String.format("Skill with id = %d not found", id));
            }
        });
    }

    private User getMentorGoal(GoalDto dto) {
        User mentor = null;
        if (dto.mentorId() != null) {
            mentor = userRepository.findById(dto.mentorId()).orElse(null);
        }
        return mentor;
    }

    private Goal getParentGoal(GoalDto dto) {
        Goal parentGoal = null;
        if (dto.parent() != null) {
            parentGoal = goalRepository.findById(dto.parent()).orElse(null);
        }
        return parentGoal;
    }

    private List<Goal> filteredGoals(List<Goal> goals, GoalFilterDto inputFilters) {
        for (Filter<Goal, GoalFilterDto> filter : goalFilters) {
            if (filter.isApplicable(inputFilters)) {
                goals = filter.apply(goals, inputFilters);
            }
        }
        return goals;
    }

}
