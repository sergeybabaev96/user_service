package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;

@RequiredArgsConstructor
@Component
public class ParticipantValidator {
    private final EventParticipationRepository eventParticipationRepository;

    private boolean isParticipantnRegistred(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void checkParticipantAlreadyRegistered(long eventId, long userId) {
        if (isParticipantnRegistred(eventId, userId)) {
            throw new EntityNotFoundException("Пользователь уже зарегистрирован!");
        }
    }

    public void checkParticipantNotRegistered(long eventId, long userId) {
        if (!isParticipantnRegistred(eventId, userId)) {
            throw new BusinessException("Пользователь не ЗАРЕГИСТРИРОВАН на событие!");
        }
    }
}
