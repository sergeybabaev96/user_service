package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.WorkScheduleService;
import school.faang.user_service.util.WorkScheduleDTOValidator;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    public WorkScheduleDto addWorkSchedule(Long userId, WorkScheduleDto workScheduleDto) {
        validateDTO (workScheduleDto);
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(Long userId, WorkScheduleDto workScheduleDto) {
        validateDTO (workScheduleDto);
        return workScheduleService.updateWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto getById(Long workScheduleId){
        return workScheduleService.getById(workScheduleId);
    }

    private void validateDTO (WorkScheduleDto workScheduleDto){
        WorkScheduleDTOValidator.checkValidTimeLine(workScheduleDto);
        WorkScheduleDTOValidator.checkValidFields(workScheduleDto);
    }

}
