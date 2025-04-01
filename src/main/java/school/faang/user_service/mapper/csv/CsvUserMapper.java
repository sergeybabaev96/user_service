package school.faang.user_service.mapper.csv;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.csv.CsvUserDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.PreviousEducation;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface CsvUserMapper {

    @Mapping(target = "username", expression = "java(dto.getFirstName() + \" \" + dto.getLastName())")
    @Mapping(target = "country", ignore = true)
    User toUser(CsvUserDto dto);

    @Mapping(target = "institution", source = "institution")
    @Mapping(target = "educationLevel", source = "degree") // маппим "степень" в уровень образования
    @Mapping(target = "specialization", source = "major")  // major или faculty — по ситуации
    @Mapping(target = "yearFrom", source = "admissionDate", qualifiedByName = "extractYear")
    @Mapping(target = "yearTo", source = "graduationDate", qualifiedByName = "extractYear")
    Education toEducation(CsvUserDto dto);

    PreviousEducation toPreviousEducation(CsvUserDto dto);

    @Named("extractYear")
    static Integer extractYear(java.time.LocalDate date) {
        return date != null ? date.getYear() : null;
    }
}
