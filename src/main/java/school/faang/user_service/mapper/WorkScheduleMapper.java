package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.WorkSchedule;

import static org.mapstruct.ap.internal.gem.MappingConstantsGem.ComponentModelGem.SPRING;

@Mapper(componentModel = SPRING)
public interface WorkScheduleMapper {

    public WorkSchedule toWorkSchedule(WorkScheduleDto workScheduleDto);

    public WorkScheduleDto toWorkScheduleDto(WorkSchedule workSchedule);
}
