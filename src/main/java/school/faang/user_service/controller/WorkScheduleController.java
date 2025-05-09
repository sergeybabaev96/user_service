package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.WorkScheduleService;
import school.faang.user_service.util.WorkScheduleDTOValidator;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    WorkScheduleService workScheduleService;

    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        validateDTO (workScheduleDto);
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        validateDTO (workScheduleDto);
        return workScheduleService.updateWorkSchedule(userId, workScheduleDto);
    }

    WorkScheduleDto getById(long workScheduleId){
        return workScheduleService.getById(workScheduleId);
    }

    private void validateDTO (WorkScheduleDto workScheduleDto){
        WorkScheduleDTOValidator.checkValidTimeLine(workScheduleDto);
        WorkScheduleDTOValidator.checkValidFields(workScheduleDto);
    }

}
