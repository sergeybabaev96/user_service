package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Event.OwnerFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OwnerFilterTest {

    @Test
    void testMatchesWhenOwnerIdMatches() {
        User owner = User.builder().id(123L).build();
        Event event = Event.builder().owner(owner).build();
        OwnerFilter filter = new OwnerFilter(123L);

        assertTrue(filter.matches(event));
    }

    @Test
    void testMatchesWhenFilterOwnerIdIsNull() {
        Event event = Event.builder().owner(User.builder().id(456L).build()).build();
        OwnerFilter filter = new OwnerFilter(null);

        assertTrue(filter.matches(event));
    }

    @Test
    void testMatchesWhenOwnerIdDoesNotMatch() {
        User owner = User.builder().id(123L).build();
        Event event = Event.builder().owner(owner).build();
        OwnerFilter filter = new OwnerFilter(456L);

        assertFalse(filter.matches(event));
    }
}
