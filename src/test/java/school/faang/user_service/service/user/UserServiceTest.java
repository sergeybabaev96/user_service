package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.user.UserFilter;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.TariffMapperImpl;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.tariff.TariffService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private UserServiceProperties userServiceProperties;

    @Mock
    private TariffService tariffService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private TariffMapperImpl tariffMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> userFilters;

    @BeforeEach
    public void setUp() {
        reset(tariffService);
        reset(userRepository);
        reset(tariffMapper);
        reset(userMapper);
        reset(userServiceProperties);
    }

    @Test
    void buyUserTariffSuccess() {
        TariffDto tariffDto = TariffDto.builder().build();
        long userId = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));

        Tariff tariff = Tariff.builder().build();
        tariffDto.setUserId(userId);

        when(tariffService.buyTariff(eq(tariffDto), eq(userId)))
                .thenReturn(Tariff.builder().build());

        User user = User.builder().tariff(tariff).build();
        when(userRepository.save(eq(user))).thenReturn(user);

        TariffDto response = userService.buyUserTariff(tariffDto, userId);

        verify(userRepository).findById(eq(userId));
        verify(tariffService).buyTariff(eq(tariffDto), eq(userId));
        verify(userRepository).save(eq(user));
        Assertions.assertNotNull(response);
    }

    @Test
    void buyUserTariffFail() {
        TariffDto tariffDto = TariffDto.builder().build();
        long userId = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder()
                .tariff(Tariff.builder().isActive(true).build())
                .build()));

        try {
            userService.buyUserTariff(tariffDto, userId);
        } catch (BusinessException e) {
            Assertions.assertEquals("User already has active tariff", e.getMessage());
        }

        verify(userRepository).findById(eq(userId));
    }
}