package school.faang.user_service.validator;

import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalTime;

public class WorkScheduleValidator {
    public static void validateWorkScheduleTimes(WorkScheduleDto workScheduleDto) {
        LocalTime startTime = workScheduleDto.getStartTime();
        LocalTime startLunch = workScheduleDto.getStartLunch();
        LocalTime endLunch = workScheduleDto.getEndLunch();
        LocalTime endTime = workScheduleDto.getEndTime();

        if (!(startTime.isBefore(startLunch) && startLunch.isBefore(endLunch) && endLunch.isBefore(endTime))) {
            throw new DataValidationException("Incorrect working hours");
        }
    }
}
