package school.faang.user_service.messaging.event;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public class EventStartEvent implements Serializable {
    private String eventId;
    private List<String> participantIds;

    public EventStartEvent(String eventId, List<String> participantIds) {
        this.eventId = eventId;
        this.participantIds = participantIds;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }
}
