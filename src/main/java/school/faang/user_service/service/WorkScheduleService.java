package school.faang.user_service.service;

import school.faang.user_service.dto.WorkScheduleDto;

public interface WorkScheduleService {

    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    WorkScheduleDto getById(long workScheduleId);
}
