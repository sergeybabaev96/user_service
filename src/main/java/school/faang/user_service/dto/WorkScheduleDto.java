package school.faang.user_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class WorkScheduleDto {
    private long id;
    private LocalTime startTime;
    private LocalTime endTIme;
    private LocalTime startLunch;
    private LocalTime endLunch;
    private String timezone; // часовой пояс в формате IANA (например, "Europe/Moscow")
}
