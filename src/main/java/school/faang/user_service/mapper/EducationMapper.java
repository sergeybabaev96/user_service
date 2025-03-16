package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education education);

    @Mapping(target = "user", ignore = true)
    void updateEducationFromDto(EducationDto dto, @MappingTarget Education entity);

    @Mapping(source = "educationDto.id", target = "id") // Добавляем аннотацию
    Education toEducationWithUser(EducationDto educationDto, User user);
}