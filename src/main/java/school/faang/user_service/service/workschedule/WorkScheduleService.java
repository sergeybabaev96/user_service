package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.dto.WorkScheduleUtils;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;


    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        validateWorkScheduleTimes(workScheduleDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Пользователь не найден"));

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        WorkScheduleUtils.setUser(workSchedule, user);
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        validateWorkScheduleTimes(workScheduleDto);

        WorkSchedule existingWorkSchedule = workScheduleRepository.findById(workScheduleDto.getId())
                .orElseThrow(() -> new DataValidationException("График работы не найден"));

        if (existingWorkSchedule.getUser().getId() != userId) {
            throw new DataValidationException("Несоответствие пользователя");
        }

        WorkSchedule updatedWorkSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        WorkScheduleUtils.setId(updatedWorkSchedule, existingWorkSchedule.getId());
        WorkScheduleUtils.setUser(updatedWorkSchedule, existingWorkSchedule.getUser());
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(updatedWorkSchedule);

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new DataValidationException("График работы не найден"));
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    private void validateWorkScheduleTimes(WorkScheduleDto workScheduleDto) {
        LocalTime startTime = workScheduleDto.getStartTime();
        LocalTime startLunch = workScheduleDto.getStartLunch();
        LocalTime endLunch = workScheduleDto.getEndLunch();
        LocalTime endTime = workScheduleDto.getEndTime();

        if (!(startTime.isBefore(startLunch) && startLunch.isBefore(endLunch) && endLunch.isBefore(endTime))) {
            throw new DataValidationException("Неверное время рабочего графика");
        }
    }
}