package school.faang.user_service.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
    MENTORSHIP_ACCEPTED("mentorshipAccepted");

    private final String key;
}
