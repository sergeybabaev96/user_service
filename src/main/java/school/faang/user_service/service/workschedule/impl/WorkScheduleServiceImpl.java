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

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;


    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        LocalTime startTime = workScheduleDto.getStartTime();
        LocalTime startLunch = workScheduleDto.getStartLunch();
        LocalTime endLunch = workScheduleDto.getEndLunch();
        LocalTime endTime = workScheduleDto.getEndTime();
        if (!(startTime.isBefore(startLunch) && startLunch.isBefore(endLunch) && endLunch.isBefore(endTime))) {
            throw new DataValidationException(TIME_EXCEPTION, TIME_EXCEPTION_CODE);
        }
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new DataValidationException(USER_DOESNT_EXIST,USER_DOESNT_EXIST_CODE);
        }
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user.get());
        WorkSchedule savedSchedule = workScheduleRepository.save(workSchedule);
        WorkScheduleDto savedWorkScheduleDto = workScheduleMapper.toWorkScheduleDto(savedSchedule);
        return savedWorkScheduleDto;
    }


}
