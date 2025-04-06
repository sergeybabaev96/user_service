package school.faang.user_service.controller.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.WorkScheduleService;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        return workScheduleService.updateWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto getWorkScheduleById(long workScheduleId) {
        return workScheduleService.getWorkScheduleById(workScheduleId);
    }
}
