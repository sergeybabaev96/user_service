package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.WorkScheduleService;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {
    /**
     * todo Проверьте, что поля из workScheduleDto удовлетворяют следующим критериям:
     * startTime < (меньше/раньше чем) startLunch < endLunch < endTime,
     * а если нет, то выбросите исключение DataValidationException (необходимо создать его самостоятельно,
     * если его ещё нет в проекте).
     **/

    WorkScheduleService workScheduleService;

    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        if (hasValidTimeLine(workScheduleDto)) {
            return workScheduleService.addWorkSchedule(userId, workScheduleDto);
        } else {
            throw new DataValidationException("startTime should be before startLunch. " +
                    "both of them should be before endLunch. And all of them should be before endTime");
        }
    }

    private boolean hasValidTimeLine(WorkScheduleDto workScheduleDto) {
        return workScheduleDto.getStartTime().isBefore(workScheduleDto.getStartLunch())
                && workScheduleDto.getStartLunch().isBefore(workScheduleDto.getEndLunch())
                && workScheduleDto.getEndLunch().isBefore(workScheduleDto.getEndTIme());
    }
}
