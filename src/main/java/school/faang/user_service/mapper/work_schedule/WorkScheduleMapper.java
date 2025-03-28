package school.faang.user_service.mapper.work_schedule;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.work_schedule.WorkScheduleDto;
import school.faang.user_service.entity.WorkSchedule;

import static org.mapstruct.ap.internal.gem.MappingConstantsGem.ComponentModelGem.SPRING;

@Mapper(componentModel = SPRING)
public interface WorkScheduleMapper {

    WorkSchedule toEntity(WorkScheduleDto workScheduleDto);

    WorkScheduleDto toDto(WorkSchedule workSchedule);
}
