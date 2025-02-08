package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private FollowerServiceImpl followerService;

    @Test
    public void testGetFollowersByUserId() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getFollowee()));
        when(subscriptionRepository.findByFolloweeId(eq(1L))).thenReturn(getFollowers());


        List<UserDto> followersByUserId = followerService.getFollowersByUserId(1L);

        assertEquals(List.of(getFollowerDto()), followersByUserId);
    }

    @Test
    public void testGetFollowersByUserIdWhenUserNotFound() {
        when(userRepository.findById(eq(1L))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> followerService.getFollowersByUserId(1L));
    }

    private UserDto getFollowerDto() {
        return new UserDto(2L, null, null, null);
    }

    private User getFollowee() {
        return User.builder().id(1L).followers(getFollowers()).build();
    }

    private List<User> getFollowers() {
        return Arrays.asList(User.builder().id(2L).username(null).email(null).phone(null).build());
    }

}