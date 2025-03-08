package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    @Mapping(target = "user", ignore = true)
    Career toCareer(CareerDto careerDto);

    @Mapping(source = "dateFrom", target = "from")
    @Mapping(source = "dateTo", target = "to")
    CareerDto toCareerDto(Career career);
}
