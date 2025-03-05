package school.faang.user_service.service.workschedule;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityAlreadyExistsException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;

    public WorkSchedule addWorkSchedule(long userId, WorkSchedule workSchedule) {
        if (workScheduleRepository.existsById(workSchedule.getId()))
            throw new EntityAlreadyExistsException("The work schedule already exists");
        return workScheduleRepository.save(getInstance(userId, workSchedule));
    }

    public WorkSchedule updateWorkSchedule(long userId, WorkSchedule workSchedule) {
        if (!workScheduleRepository.existsById(workSchedule.getId()))
            throw new EntityNotFoundException("The work schedule not found");
        workScheduleRepository.delete(workSchedule);
        return workScheduleRepository.save(getInstance(userId, workSchedule));
    }

    private WorkSchedule getInstance(long userId, WorkSchedule workSchedule) {
        if (!(workSchedule.getStartTime().isBefore(workSchedule.getStartLunch())
                && workSchedule.getStartLunch().isBefore(workSchedule.getEndLunch())
                && workSchedule.getEndLunch().isBefore(workSchedule.getEndTime())))
            throw new DataValidationException("The work schedule time is invalid");
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("User not founds");

        return WorkSchedule.builder()
                .startTime(workSchedule.getStartTime())
                .startLunch(workSchedule.getStartLunch())
                .endLunch(workSchedule.getEndLunch())
                .endTime(workSchedule.getEndTime())
                .user(userRepository.getReferenceById(userId))
                .build();
    }

    public WorkSchedule getWorkScheduleById(long id) {
        return workScheduleRepository.getReferenceById(id);
    }
}