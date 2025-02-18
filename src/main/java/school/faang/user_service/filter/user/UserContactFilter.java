package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserContactFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (!validateParameters(users, filters) || filters == null || filters.getContactPattern() == null) {
            return Stream.empty();
        }

        return users.filter(user -> user.getContacts() != null && user.getContacts().stream()
                .anyMatch(contact -> contact.getContact() != null && contact.getContact()
                        .toUpperCase()
                        .contains(filters.getContactPattern().toUpperCase()))
        );
    }
}
