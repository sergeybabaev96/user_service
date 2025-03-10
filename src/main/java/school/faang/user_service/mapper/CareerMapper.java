package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {

    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    @Mapping(source = "UserId", target = "user")
    Career toCareer(CareerDto careerDto);

    @Mapping(source = "dateFrom", target = "from")
    @Mapping(source = "dateTo", target = "to")
    @Mapping(source = "user", target = "UserId")
    CareerDto toCareerDto(Career career);

    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    @Mapping(source = "UserId", target = "user")
    Career UpdatedCareer(@MappingTarget Career career, CareerDto careerDto);
}

