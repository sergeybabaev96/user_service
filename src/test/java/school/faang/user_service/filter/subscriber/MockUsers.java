package school.faang.user_service.filter.subscriber;

import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class MockUsers {

    public User user1 = new User();
    private User user2 = new User();
    private User user3 = new User();

    public MockUsers() {
        user1.setUsername("Ramil");
        user1.setExperience(5);
        user1.setPhone("123-456");

        user2.setUsername("Albert");
        user2.setExperience(1);
        user2.setPhone("000-000");

        user3.setUsername("Kamil");
        user3.setExperience(15);
        user3.setPhone("111-111");
    }

    public Stream<User> getUsers() {
        return Stream.of(user1, user2, user3);
    }
}
