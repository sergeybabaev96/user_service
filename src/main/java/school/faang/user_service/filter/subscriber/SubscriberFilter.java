package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface SubscriberFilter {

    Stream<User> apply(Stream<User> users, UserFilterDto filters);

    boolean isApplicable(UserFilterDto filters);

}
