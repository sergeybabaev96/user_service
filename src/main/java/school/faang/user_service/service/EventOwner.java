package school.faang.user_service.service;

import school.faang.user_service.entity.User;

public interface EventOwner {
    User getOwner(Long id);
}
