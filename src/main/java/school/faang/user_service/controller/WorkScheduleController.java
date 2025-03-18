package school.faang.user_service.controller;


import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@RestController
@RequestMapping("/work_schedule")
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @PostMapping("/add_schedule")
    public WorkScheduleDto addWorkSchedule(@RequestParam long userId, @RequestBody WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }
}
