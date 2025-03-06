package school.faang.user_service.mapper;

import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

public interface EducationMapper {

    Education toEducation(EducationDto educationDto);


    EducationDto toEducationDto(Education education);
}
