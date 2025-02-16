package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.subscriber.SubscriberReadDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface SubscriberMapper {
    SubscriberReadDto toDto(User user);
}