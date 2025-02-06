package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface TariffMapper {

    @Mapping(target = "user", expression = "java(mapUser(tariffDto.getUserId()))")
    @Mapping(target = "event", expression = "java(mapEvent(tariffDto.getEventId()))")
    Tariff toEntity(TariffDto tariffDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    TariffDto toDto(Tariff tariff);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Event mapEvent(Long eventId) {
        if (eventId == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventId);
        return event;
    }


}
