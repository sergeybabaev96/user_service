package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.entity.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PersonEducationUserMapper {

    @Named("mapEducation")
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "yearsOfStudy", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "GPA", ignore = true)
    Person.Education toEducation(User user);
}
