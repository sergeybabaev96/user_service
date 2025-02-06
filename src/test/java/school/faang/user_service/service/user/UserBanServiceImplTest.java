package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBanServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserBanServiceImpl userBanService;

    @Test
    public void testBanUsers() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUser()));
        when(userRepository.save(eq(getBannedUser()))).thenReturn(getBannedUser());

        userBanService.banUsers(String.valueOf(1L));

        verify(userRepository).findById(eq(1L));
        verify(userRepository).save(eq(getBannedUser()));

    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .build();
    }

    private User getBannedUser() {
        return User.builder()
                .id(1L)
                .banned(true)
                .build();
    }

}