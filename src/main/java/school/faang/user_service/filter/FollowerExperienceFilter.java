package school.faang.user_service.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
@Slf4j
public class FollowerExperienceFilter implements UserFollowersFilter {
    @Override
    public boolean isApplicable(User follower, UserFilterDto filter) {
        return follower.getExperience() != null;
    }

    @Override
    public boolean test(User follower, UserFilterDto filter) {
        return follower.getExperience() <= filter.getExperienceMax()
                && follower.getExperience() >= filter.getExperienceMin();
    }
}
