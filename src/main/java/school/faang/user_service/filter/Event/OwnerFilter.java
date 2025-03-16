package school.faang.user_service.filter.Event;

import school.faang.user_service.entity.event.Event;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OwnerFilter implements EventFilter {
    private final Long ownerId;

    @Override
    public boolean matches(Event event) {
        return ownerId == null || event.getOwner().getId().equals(ownerId);
    }
}
