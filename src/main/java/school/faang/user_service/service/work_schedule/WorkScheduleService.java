package school.faang.user_service.service.work_schedule;

import school.faang.user_service.dto.work_schedule.WorkScheduleDto;

public interface WorkScheduleService {

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    public WorkScheduleDto getById(long workScheduleId);
}
