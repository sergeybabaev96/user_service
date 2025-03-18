package school.faang.user_service.service.workschedule.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import school.faang.user_service.service.workschedule.WorkScheduleService;

import java.time.LocalTime;
import java.util.Optional;

import static school.faang.user_service.exception.DataValidationException.TIME_EXCEPTION;
import static school.faang.user_service.exception.DataValidationException.TIME_EXCEPTION_CODE;
import static school.faang.user_service.exception.DataValidationException.USER_DOESNT_EXIST;
import static school.faang.user_service.exception.DataValidationException.USER_DOESNT_EXIST_CODE;
import static school.faang.user_service.exception.DataValidationException.USER_DOESNT_HAVE_ACCESS;
import static school.faang.user_service.exception.DataValidationException.USER_DOESNT_HAVE_ACCESS_CODE;
import static school.faang.user_service.exception.DataValidationException.WORK_SCHEDULE_DOESNT_EXIST;
import static school.faang.user_service.exception.DataValidationException.WORK_SCHEDULE_DOESNT_EXIST_CODE;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;


    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) throws DataValidationException {
        Optional<User> user = userRepository.findById(userId);
        checkData(workScheduleDto);
        if(user.isEmpty()) {
            throw new DataValidationException(USER_DOESNT_EXIST,USER_DOESNT_EXIST_CODE);
        }
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user.get());
        WorkSchedule savedSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toWorkScheduleDto(savedSchedule);
    }

    @Override
    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        checkData(workScheduleDto);
        long workScheduleId = workScheduleDto.getId();
        Optional<WorkSchedule> workScheduleById = workScheduleRepository.findById(workScheduleId);
        if(workScheduleById.isEmpty()) {
            throw new DataValidationException(WORK_SCHEDULE_DOESNT_EXIST,WORK_SCHEDULE_DOESNT_EXIST_CODE);
        }
        if(workScheduleId != userId) {
            throw new DataValidationException(USER_DOESNT_HAVE_ACCESS,USER_DOESNT_HAVE_ACCESS_CODE);
        }
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(workScheduleById.get().getUser());
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
    }

    private void checkData (WorkScheduleDto workScheduleDto) throws DataValidationException {
        LocalTime startTime = workScheduleDto.getStartTime();
        LocalTime startLunch = workScheduleDto.getStartLunch();
        LocalTime endLunch = workScheduleDto.getEndLunch();
        LocalTime endTime = workScheduleDto.getEndTime();
        if (!(startTime.isBefore(startLunch) && startLunch.isBefore(endLunch) && endLunch.isBefore(endTime))) {
            throw new DataValidationException(TIME_EXCEPTION, TIME_EXCEPTION_CODE);
        }
    }


}
