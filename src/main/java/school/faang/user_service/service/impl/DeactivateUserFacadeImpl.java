package school.faang.user_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.service.DeactivateUserFacade;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.mentorship.MentorshipService;

@RequiredArgsConstructor
@Service
public class DeactivateUserFacadeImpl implements DeactivateUserFacade {

    private final UserService userService;
    private final MentorshipService mentorshipService;
    private final GoalService goalService;
    private final EventService eventService;

    @Override
    @Transactional
    public UserDto deactivateUser(long userId) {
        if (userService.getUser(userId).getBody() != null) {
            goalService.deactivateGoalsByUserId(userId);
            eventService.deactivateEventsByUserId(userId);
            UserDto userDto = userService.deactivateUser(userId);
            mentorshipService.deactivateMentorship(userId);
            return userDto;
        }
        else throw new UserNotFoundException("User not found");
    }
}
