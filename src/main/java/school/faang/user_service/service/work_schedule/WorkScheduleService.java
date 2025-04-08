package school.faang.user_service.service.work_schedule;

import school.faang.user_service.dto.work_schedule.WorkScheduleDto;

public interface WorkScheduleService {

    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    WorkScheduleDto getById(long workScheduleId);
}
