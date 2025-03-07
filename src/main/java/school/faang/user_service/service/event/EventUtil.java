package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import java.time.LocalDate;
import java.util.List;

public class EventUtil {

    public boolean isValid(EventDTO event) {
        LocalDate today = LocalDate.now();
        if (event == null) {
            throw new DataValidationException("Event is null");
        }
        if (event.getTitle().isBlank()) {
            throw new DataValidationException("Title is blank");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("Owner id is null");
        }
        if (event.getStartDate().toLocalDate().isBefore(today)) {
            throw new DataValidationException("Start date is before today");
        }
        return true;
    }

    public boolean checkOwnerSkills(User eventOwner, EventDTO event) {
        if (event.getRelatedSkills() != null) {
            //преобразуем навыки пользователя в их id, для дальнейшего сравнения
            List<Long> ownerSkillsIDs = eventOwner.getSkills().stream()
                    .map(Skill::getId)
                    .toList();
            //Проверяем, есть ли в навыках пользователя навык, который связан с объявленными навыками события
            return event.getRelatedSkills().stream()
                    .anyMatch(ownerSkillsIDs::contains);
        } else {
            return false;
        }
    }
}
