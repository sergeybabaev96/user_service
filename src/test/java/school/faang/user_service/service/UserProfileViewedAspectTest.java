package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.service.kafka.UserProfileViewedProducer;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserProfileViewedAspectTest {

    @Mock
    private UserContext userContext;

    @Mock
    private UserProfileViewedProducer producer;

    @InjectMocks
    private UserProfileViewedAspect aspect;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(2L);
        ContactPreference contactPreference = new ContactPreference();
        contactPreference.setPreference(PreferredContact.EMAIL);
        user.setContactPreference(contactPreference);
    }

    @Test
    void testAfterUserProfileViewedRequestingUserIsValidUserHasContactPreference() {
        when(userContext.getUserId()).thenReturn(1L);

        aspect.afterUserProfileViewed(user);

        verify(producer, times(1)).sendMessage(1L, 2L);
    }

    @Test
    void testAfterUserProfileViewedRequestingUserIsZeroNotSendMessage() {
        when(userContext.getUserId()).thenReturn(0L);

        aspect.afterUserProfileViewed(user);

        verify(producer, never()).sendMessage(anyLong(), anyLong());
    }

    @Test
    void testAfterUserProfileViewedUserHasNoContactPreferenceNotSendMessage() {
        when(userContext.getUserId()).thenReturn(1L);
        user.setContactPreference(null);

        aspect.afterUserProfileViewed(user);

        verify(producer, never()).sendMessage(anyLong(), anyLong());
    }

    @Test
    void testAfterUserProfileViewedUserHasNoContactNotSendMessage() {
        ContactPreference contactPreference = new ContactPreference();
        contactPreference.setPreference(null);
        user.setContactPreference(contactPreference);

        when(userContext.getUserId()).thenReturn(1L);

        aspect.afterUserProfileViewed(user);

        verify(producer, never()).sendMessage(anyLong(), anyLong());
    }
}
