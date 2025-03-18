package school.faang.user_service.controller.work_schedule;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.work_schedule.WorkScheduleDto;
import school.faang.user_service.service.work_schedule.WorkScheduleService;

import static school.faang.user_service.controller.work_schedule.ControllerConstant.*;

@RestController
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @PostMapping(ADD_WORK_SCHEDULE_PATH)
    public WorkScheduleDto addWorkSchedule(@RequestHeader long userId, @RequestBody WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    @PostMapping(UPDATE_WORK_SCHEDULE_PATH)
    public WorkScheduleDto updateWorkSchedule(@RequestHeader long userId, @RequestBody WorkScheduleDto workScheduleDto) {
        return workScheduleService.updateWorkSchedule(userId, workScheduleDto);
    }

    @GetMapping(FIND_SCHEDULE_BY_ID)
    public WorkScheduleDto getById(@RequestHeader long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }

}
