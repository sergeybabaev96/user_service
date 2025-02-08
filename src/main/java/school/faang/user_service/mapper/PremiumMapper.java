package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PremiumMapper {
    Premium toEntity(PremiumDto premiumDto);

    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "userId", source = "user.id")
    PremiumDto toDto(Premium premium);
}