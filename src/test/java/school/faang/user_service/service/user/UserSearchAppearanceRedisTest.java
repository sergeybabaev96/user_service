package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.BaseTest;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.queue.SearchAppearanceEvent;
import school.faang.user_service.dto.user.GetUserRequest;
import school.faang.user_service.dto.user.UserFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.queue.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSearchAppearanceRedisTest extends BaseTest {

    @Autowired
    private UserServiceImpl userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    @MockBean
    private UserContext userContext;
    @MockBean
    private List<UserFilter> userFilter;


    private SearchAppearanceEvent searchAppearanceEvent;
    private List<User> foundUsers;

    @BeforeEach
    public void setUp() {
        reset(userRepository, searchAppearanceEventPublisher, userContext, userFilter);
        searchAppearanceEvent = SearchAppearanceEvent.builder().foundUserId(2L).build();
        foundUsers = List.of(User.builder()
                .id(2L)
                .build());
    }

    @Test
    public void findUsersByFilterDecrementShows() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.findAllOrderByTariffAndLimit(1, 1))
                .thenReturn(foundUsers);
        GetUserRequest request = new GetUserRequest();
        request.setOffset(1);
        request.setLimit(1);
        userService.findUsersByFilter(request);
        verify(searchAppearanceEventPublisher, times(1)).publish(any());
    }
}


