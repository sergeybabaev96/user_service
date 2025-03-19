package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Event.TitleFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


class TitleFilterTest {

    @Test
    void testMatchesWhenTitleContainsSubstringIgnoringCase() {
        Event event = Event.builder().title("Spring Webinar 2026").build();
        TitleFilter filter = new TitleFilter("webinar");

        assertTrue(filter.matches(event));
    }

    @Test
    void testMatchesWhenFilterTitleIsNull() {
        Event event = Event.builder().title("Any Title").build();
        TitleFilter filter = new TitleFilter(null);

        assertTrue(filter.matches(event));
    }

    @Test
    void testMatchesWhenTitleDoesNotContainSubstring() {
        Event event = Event.builder().title("Java Conference").build();
        TitleFilter filter = new TitleFilter("meeting");

        assertFalse(filter.matches(event));
    }

    @Test
    void testMatchesWhenEventTitleIsNull() {
        Event event = Event.builder().title("Some title").build();
        TitleFilter filter = new TitleFilter(null);

        assertTrue(filter.matches(event));
    }
}
