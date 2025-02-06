package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import school.faang.user_service.BaseTest;
import school.faang.user_service.dto.user.GetUserRequest;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.repository.TariffRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.tariff.TariffService;

import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceIntegrationTest extends BaseTest {

    @Autowired
    private UserService userService;

    @SpyBean
    private TariffService tariffService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TariffMapper tariffMapper;
    @MockBean
    private TariffRepository tariffRepository;

    @BeforeEach
    public void setUp() {
        reset(tariffService, tariffMapper, userRepository, tariffRepository);
    }

    @Test
    public void findUsersByFilterDecrementShows() {

        GetUserRequest request = new GetUserRequest();
        request.setFilter(UserDto.builder().build());
        request.setLimit(1);
        request.setOffset(0);

        Tariff tariff = Tariff.builder()
                .isActive(true)
                .id(1L)
                .shows(1)
                .priority(1)
                .build();
        when(userRepository.findAllOrderByTariffAndLimit(1, 0))
                .thenReturn(List.of(User.builder()
                                .id(1L)
                                .tariff(tariff)
                        .build()));
        when(tariffRepository.existsById(1L)).thenReturn(true);

        userService.findUsersByFilter(request);

        verify(tariffService).decrementShows(tariff);
        verify(tariffRepository).save(Tariff.builder()
                .isActive(false)
                .id(1L)
                .shows(0)
                .priority(1)
                .build());
    }
}
