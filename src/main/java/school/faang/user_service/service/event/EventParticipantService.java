package school.faang.user_service.service.event;

import school.faang.user_service.dto.user.UserParticipantDto;

import java.util.List;

public interface EventParticipantService {

    void registerParticipant(long eventId, long userId);

    void unregisterParticipant(long eventId, long userId);

    List<UserParticipantDto> findAllParticipantByEventId(long eventId);

    int countParticipant(long eventId);
}
