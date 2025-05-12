package school.faang.user_service.service.event;

import school.faang.user_service.entity.User;

import java.util.List;

public interface EventParticipationService {
    public void registerParticipant(long eventId, long userId);

    public void unregisterParticipant(long eventId, long userId);

    public List<User> getParticipant(long eventId);

    public long getParticipantsCount(long eventId);
}