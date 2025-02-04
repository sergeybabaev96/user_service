package school.faang.user_service.service.user;

import org.apache.commons.lang3.tuple.Pair;
import school.faang.user_service.entity.User;

public interface AvatarService {

    Pair<String, String> saveAvatarsToMinio(User user);

}
