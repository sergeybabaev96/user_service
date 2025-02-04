package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring")
public interface PremiumMapper {
    @Mapping(source = "userId", target = "user.id")
    Premium toEntity(PremiumDto premiumDto);

    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium premium);
}
