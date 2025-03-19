package school.faang.user_service.dto.work_schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class WorkScheduleDto {

    private Long id;
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime endTime;
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime startLunch;
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime endLunch;
    private String timezone;
}
