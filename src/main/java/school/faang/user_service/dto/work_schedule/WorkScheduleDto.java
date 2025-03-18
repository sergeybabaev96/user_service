package school.faang.user_service.dto.work_schedule;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class WorkScheduleDto {

    private long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime startLunch;
    private LocalTime endLunch;
    private String timezone;
}
