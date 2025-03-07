package school.faang.user_service.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;

@Component
public class UserFilterImpl implements UserFilter {

    @Override
    public boolean apply(User user, Object o) {
        UserFilterRequest filter = (UserFilterRequest) o;

        String namePattern = filter.namePattern();
        String phonePattern = filter.phonePattern();
        int experienceMin = filter.experienceMin();
        int experienceMax = filter.experienceMax();

        if (experienceMax < experienceMin) {
            throw new DataValidationException("ExperienceMin не может быть больше ExperienceMax");
        }

        boolean isMatchByName = true;
        if (namePattern != null && !namePattern.isBlank()) {
            isMatchByName = user.getUsername().equals(namePattern);
        }

        boolean isMatchByPhone = true;
        if (phonePattern != null && !phonePattern.isBlank()) {
            isMatchByPhone = user.getPhone().equals(phonePattern);
        }

        boolean isMatchByExperience = true;
        Integer userExperience = user.getExperience();
        if (userExperience != null) {
            if (experienceMin > 0) {
                isMatchByExperience = userExperience >= experienceMin;
            }
            if (experienceMax > 0) {
                isMatchByExperience = userExperience <= experienceMax;
            }
        }

        return isMatchByName && isMatchByPhone && isMatchByExperience;
    }
}
