package school.faang.user_service.controller.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

import java.net.URI;

@RestController
@RequestMapping("/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<WorkScheduleDto> addWorkSchedule(@PathVariable long userId, @RequestBody WorkScheduleDto workScheduleDto) {
        WorkScheduleDto savedWorkSchedule = workScheduleService.addWorkSchedule(userId, workScheduleDto);
        return ResponseEntity.created(URI.create("/work-schedules/" + savedWorkSchedule.getId()))
                .body(savedWorkSchedule);
    }
    @PutMapping("/users/{userId}/schedules/{workScheduleId}")
    public ResponseEntity<WorkScheduleDto> updateWorkSchedule(
            @PathVariable long userId,
            @RequestBody WorkScheduleDto workScheduleDto) {
        WorkScheduleDto updatedWorkSchedule = workScheduleService.updateWorkSchedule(userId, workScheduleDto);
        return ResponseEntity.ok(updatedWorkSchedule);
    }
    @GetMapping("/{workScheduleId}")
    public ResponseEntity<WorkScheduleDto> getById(@PathVariable long workScheduleId) {
        WorkScheduleDto workSchedule = workScheduleService.getById(workScheduleId);
        return ResponseEntity.ok(workSchedule);
    }
}
