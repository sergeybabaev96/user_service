package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

/**
 * Класс для валидации создания и обновления целей.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoalValidator {
    private static final int MAX_GOALS = 3;
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    /**
     * Проверяет корректность данных для создания цели
     *
     * @param userId Идентификатор пользователя, для которого создаётся цель
     * @param goal DTO с данными для создания цели
     * @throws DataValidationException если цель не прошла валидацию
     */
    public void validateCreated(Long userId, GoalCreateDto goal) {
        validateTitle(goal);
        validateMaxActiveGoals(userId);
        validateSkillsExist(goal);
    }

    /**
     * Проверяет корректность данных для обновления цели
     *
     * @param goalId Идентификатор цели, которую необходимо обновить
     * @param goal DTO с данными для обновления цели
     * @throws DataValidationException если цель не прошла валидацию
     */
    public void validateUpdated(Long goalId, GoalCreateDto goal) {
        validateStatus(goalId);
        validateTitle(goal);
        validateSkillsExist(goal);
    }

    /**
     * Проверка, что название цели не null и не пустое
     *
     * @param goal DTO с данными цели
     * @throws DataValidationException если название цели null или пустое
     */
    private void validateTitle(GoalCreateDto goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            log.error("Ошибка валидации: название цели пустое");
            throw new DataValidationException("Название цели пустое");
        }
    }

    /**
     * Проверка, что у пользователя не более 2 активных целей на момент добавления новой цели
     *
     * @param userId Идентификатор пользователя
     * @throws DataValidationException если пользователь не найден или у него 3 или более активные цели
     */
    private void validateMaxActiveGoals(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Ошибка валидации: пользователь не найден");
            throw new DataValidationException("Пользователь не найден: " + userId);
        }
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS) {
            log.error("Ошибка валидации: максимальное количество активных целей 3");
            throw new DataValidationException("Превышено максимальное количество целей: " + MAX_GOALS);
        }
    }

    /**
     * Проверка, что навыки из цели существуют в базе данных
     *
     * @param goal DTO с данными цели
     * @throws DataValidationException если цель содержит несуществующие навыки
     */
    private void validateSkillsExist(GoalCreateDto goal) {
        List<Long> skillsId = goal.getSkillsToAchieveId();
        if (null != skillsId && skillRepository.countExisting(skillsId) != skillsId.size()) {
            log.error("Ошибка валидации: цель содержит несуществующие навыки");
            throw new DataValidationException("Цель содержит несуществующие навыки");
        }
    }

    /**
     * Проверка статуса цели
     *
     * @param goalId Идентификатор цели
     * @throws DataValidationException если цель не найдена или уже завершена
     */
    private void validateStatus(Long goalId) {
        if (goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Цель не найдена: " + goalId))
                .getStatus() == GoalStatus.COMPLETED) {
            log.error("Ошибка валидации: нельзя изменять завершенную цель");
            throw new DataValidationException("Цель уже завершена");
        }
    }
}
