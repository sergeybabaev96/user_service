package school.faang.user_service.service.workschedule;

import school.faang.user_service.dto.WorkScheduleDto;

public interface WorkScheduleService {

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto);
}
