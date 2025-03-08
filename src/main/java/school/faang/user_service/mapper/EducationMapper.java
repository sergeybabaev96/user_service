package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEducation(EducationDto educationDto);
    EducationDto toEducationDto(Education education);

    @Mapping(target = "user", ignore = true)
    void updateEducationFromDto(EducationDto dto, @MappingTarget Education entity);
}
