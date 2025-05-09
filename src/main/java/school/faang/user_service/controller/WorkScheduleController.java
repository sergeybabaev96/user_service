package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.WorkScheduleService;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    WorkScheduleService workScheduleService;

    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        checkValidTimeLine(workScheduleDto);
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        checkValidTimeLine(workScheduleDto);
        return workScheduleService.updateWorkSchedule(userId, workScheduleDto);
    }

    WorkScheduleDto getById(long workScheduleId){
        return workScheduleService.getById(workScheduleId);
    }

    private void checkValidTimeLine(WorkScheduleDto workScheduleDto) {
        if (workScheduleDto.getStartTime().isBefore(workScheduleDto.getStartLunch())
                && workScheduleDto.getStartLunch().isBefore(workScheduleDto.getEndLunch())
                && workScheduleDto.getEndLunch().isBefore(workScheduleDto.getEndTime())) {
            return;
        }
        throw new DataValidationException("startTime should be before startLunch. " +
                "both of them should be before endLunch. And all of them should be before endTime");
    }
}
