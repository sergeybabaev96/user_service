package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.service.user.filter.ContactFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContactFilterTest {
    private ContactFilter contactFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        contactFilter = new ContactFilter();

        userFilterDto = new UserFilterDto();
        Contact firstContact = new Contact(0, new User(), "contact", ContactType.CUSTOM);
        Contact secondCcontact = new Contact(0, new User(), "phone", ContactType.CUSTOM);


        firstUser = new User();
        firstUser.setContacts(List.of(firstContact));

        secondUser = new User();
        secondUser.setContacts(List.of(firstContact, secondCcontact));

        thirdUser = new User();
        thirdUser.setContacts(List.of(secondCcontact));

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenContactPatternIsNotNull() {
        userFilterDto.setContactPattern("contact");
        boolean isApplicable = contactFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenContactPatternIsNull() {
        userFilterDto.setContactPattern(null);
        boolean isApplicable = contactFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByContactPattern() {
        userFilterDto.setContactPattern("phone");
        List<User> filteredUsers = contactFilter.apply(users, userFilterDto);

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(secondUser));
        assertTrue(filteredUsers.contains(thirdUser));
    }
}
