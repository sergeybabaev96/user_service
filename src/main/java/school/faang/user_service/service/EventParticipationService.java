package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface EventParticipationService {

    void registerParticipant(long eventId, long userId);

    void unregister(long eventId, long userId);

    List<UserDto> getParticipant(long eventId);

    int getParticipantsCount(long eventId);
}
