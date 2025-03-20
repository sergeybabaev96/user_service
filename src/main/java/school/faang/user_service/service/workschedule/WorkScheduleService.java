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
import school.faang.user_service.validator.WorkScheduleValidator;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        WorkScheduleValidator.validateWorkScheduleTimes(workScheduleDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));

        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        WorkScheduleUtils.setUser(workSchedule, user);
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        WorkScheduleValidator.validateWorkScheduleTimes(workScheduleDto);

        WorkSchedule existingWorkSchedule = workScheduleRepository.findById(workScheduleDto.getId())
                .orElseThrow(() -> new DataValidationException("Opening hours not found"));

        if (existingWorkSchedule.getUser().getId() != userId) {
            throw new DataValidationException("User mismatch");
        }

        WorkSchedule updatedWorkSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        WorkScheduleUtils.setId(updatedWorkSchedule, existingWorkSchedule.getId());
        WorkScheduleUtils.setUser(updatedWorkSchedule, existingWorkSchedule.getUser());
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(updatedWorkSchedule);

        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(() -> new DataValidationException("Opening hours not found"));
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }
}