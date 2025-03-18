package school.faang.user_service.dto;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import java.lang.reflect.Field;

public class WorkScheduleUtils {
    public static void setUser(WorkSchedule workSchedule, User user) {
        try {
            Field userField = WorkSchedule.class.getDeclaredField("user");
            userField.setAccessible(true);
            userField.set(workSchedule, user);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось установить поле пользователя", e);
        }
    }

    public static void setId(WorkSchedule workSchedule, Long id) {
        try {
            Field idField = WorkSchedule.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(workSchedule, id);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось установить поле идентификатора", e);
        }
    }
}
