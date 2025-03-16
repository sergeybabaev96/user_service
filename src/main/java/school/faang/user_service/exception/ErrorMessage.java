package school.faang.user_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    SUBSCRIBING_ON_YOURSELF_ERROR_MSG("You can't subscribe on yourself"),
    ALREADY_SUBSCRIBED_ERROR_MSG("You are already subscribed to this user"),
    UNSUBSCRIBING_FROM_YOURSELF_ERROR_MSG("You can't unsubscribe from yourself"),
    IMPOSSIBLE_TO_UNFOLLOW_ERROR_MSG("Impossible to unfollow a user you are not following"),
    USER_DOES_NOT_EXIST_BY_ID_ERROR_MSG("User with ID {} does not exist."),
    USER_DOES_NOT_EXIST("User does not exist");

    private final String message;
}
