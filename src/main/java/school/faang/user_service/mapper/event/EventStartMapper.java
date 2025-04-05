package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.model.events.NotificationEventStartEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventStartMapper {
    @Mapping(source = "eventId", target = "eventId")
    @Mapping(source = "userIds", target = "userIds")
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "message", target = "message")
    NotificationEventStartEvent toNotificationEventStartEvent(EventStartDto dto);
}
