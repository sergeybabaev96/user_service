package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserReadDto dto);

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserReadDto toDto(User entity);
}
