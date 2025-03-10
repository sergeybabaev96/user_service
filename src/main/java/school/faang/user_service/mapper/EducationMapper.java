package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.EducationCreateDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEducation(EducationCreateDto educationDto);

    EducationCreateDto toEducationDto(Education education);
}
