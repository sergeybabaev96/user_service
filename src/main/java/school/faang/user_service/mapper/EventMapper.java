package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Identifiable;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface EventMapper {

    EventDto toDto(Event event);

    default Long toId(Identifiable identifiable) {
        if (identifiable == null) {
            return null;
        }
        return identifiable.getId();
    }
}
