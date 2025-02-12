package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberContactFilterTest {
    private Contact firstContact1, secondContact1, firstContact2, firstContact3;
    private User user1, user2, user3;
    private SubscriberContactFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberContactFilter();
        filterDto = new SubscriberFilterDto();

        firstContact1 = new Contact();
        secondContact1 = new Contact();
        firstContact2 = new Contact();
        firstContact3 = new Contact();
        firstContact1.setContact("@viktor_ovod");
        secondContact1.setContact("@paul_durov");
        firstContact2.setContact("@elon_musk");
        firstContact3.setContact("@mark_z");

        user1 = new User();
        user2 = new User();
        user3 = new User();
        user1.setContacts(List.of(firstContact1, secondContact1));
        user2.setContacts(List.of(firstContact2));
        user3.setContacts(List.of(firstContact3));
    }

    @Test
    void testIsApplicableWhenContactPatternIsSet() {
        filterDto.setContactPattern("@ma");
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenContactPatternIsNull() {
        filterDto.setContactPattern(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesOneUser() {
        filterDto.setContactPattern("rov");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        filterDto.setContactPattern("o");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        filterDto.setContactPattern("bob");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullContacts() {
        user1.setContacts(null);
        filterDto.setContactPattern("lon");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyUserWithNullValueTheContact() {
        firstContact2.setContact(null);
        user2.setContacts(List.of(firstContact2));
        filterDto.setContactPattern("o");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}