package school.faang.user_service.controller.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        WorkSchedule workScheduleEntity = workScheduleMapper.toEntity(workScheduleDto);
        return workScheduleMapper.toDto(workScheduleService.addWorkSchedule(userId, workScheduleEntity));
    }

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        WorkSchedule workScheduleEntity = workScheduleMapper.toEntity(workScheduleDto);
        return workScheduleMapper.toDto(workScheduleService.updateWorkSchedule(userId, workScheduleEntity));
    }

    public WorkScheduleDto getWorkScheduleById(long workScheduleId) {
        return workScheduleMapper.toDto(workScheduleService.getWorkScheduleById(workScheduleId));
    }
}
