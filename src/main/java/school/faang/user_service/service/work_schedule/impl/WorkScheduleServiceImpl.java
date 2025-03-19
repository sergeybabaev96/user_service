package school.faang.user_service.service.work_schedule.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.work_schedule.WorkScheduleDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.data_validation_exception.DataValidationException;
import school.faang.user_service.mapper.work_schedule.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import school.faang.user_service.service.work_schedule.WorkScheduleService;

import java.time.LocalTime;

import static school.faang.user_service.exception.data_validation_exception.DataValidationException.TIME_EXCEPTION;
import static school.faang.user_service.exception.data_validation_exception.DataValidationException.TIME_EXCEPTION_CODE;
import static school.faang.user_service.exception.data_validation_exception.DataValidationException.USER_DOESNT_EXIST;
import static school.faang.user_service.exception.data_validation_exception.DataValidationException.USER_DOESNT_EXIST_CODE;
import static school.faang.user_service.exception.data_validation_exception.DataValidationException.USER_DOESNT_HAVE_ACCESS;
import static school.faang.user_service.exception.data_validation_exception.DataValidationException.USER_DOESNT_HAVE_ACCESS_CODE;
import static school.faang.user_service.exception.data_validation_exception.DataValidationException.WORK_SCHEDULE_DOESNT_EXIST;
import static school.faang.user_service.exception.data_validation_exception.DataValidationException.WORK_SCHEDULE_DOESNT_EXIST_CODE;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;


    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException(USER_DOESNT_EXIST,USER_DOESNT_EXIST_CODE));
        checkData(workScheduleDto);
        WorkSchedule savedSchedule = saveWorkSchedule(workScheduleDto, user);
        return workScheduleMapper.toDto(savedSchedule);
    }

    @Override
    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        checkData(workScheduleDto);
        WorkSchedule workScheduleById = getWorkSchedule(workScheduleDto.getId());
        if(workScheduleById.getUser().getId() != userId) {
            throw new DataValidationException(USER_DOESNT_HAVE_ACCESS,USER_DOESNT_HAVE_ACCESS_CODE);
        }
        WorkSchedule savedWorkSchedule = saveWorkSchedule(workScheduleDto, workScheduleById.getUser());
        return workScheduleMapper.toDto(savedWorkSchedule);
    }

    @Override
    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workScheduleById = getWorkSchedule(workScheduleId) ;
        return workScheduleMapper.toDto(workScheduleById);
    }

    private void checkData (WorkScheduleDto workScheduleDto) {
        LocalTime startTime = workScheduleDto.getStartTime();
        LocalTime startLunch = workScheduleDto.getStartLunch();
        LocalTime endLunch = workScheduleDto.getEndLunch();
        LocalTime endTime = workScheduleDto.getEndTime();
        if (!(startTime.isBefore(startLunch) && startLunch.isBefore(endLunch) && endLunch.isBefore(endTime))) {
            throw new DataValidationException(TIME_EXCEPTION, TIME_EXCEPTION_CODE);
        }
    }

    private WorkSchedule getWorkSchedule(long workScheduleId) {
        return workScheduleRepository.findById(workScheduleId).orElseThrow(() -> new DataValidationException(WORK_SCHEDULE_DOESNT_EXIST,WORK_SCHEDULE_DOESNT_EXIST_CODE));
    }

    private WorkSchedule saveWorkSchedule(WorkScheduleDto workScheduleDto, User user) {
        WorkSchedule workSchedule = workScheduleMapper.toEntity(workScheduleDto);
        workSchedule.setUser(user);
        return workScheduleRepository.save(workSchedule);
    }

}
