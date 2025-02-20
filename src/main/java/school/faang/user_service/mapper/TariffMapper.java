package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.Tariff;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface TariffMapper {

    Tariff toEntity(TariffDto tariffDto);

    TariffDto toDto(Tariff tariff);

}
