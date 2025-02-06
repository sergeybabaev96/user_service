package school.faang.user_service.service.tariff;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.BaseTest;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.client.payment.PaymentServiceFeignClient;
import school.faang.user_service.common.Currency;
import school.faang.user_service.common.PaymentStatus;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.repository.TariffRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TariffServiceTest extends BaseTest {

    @Autowired
    private TariffService tariffService;

    @Autowired
    private TariffMapper tariffMapper;

    @MockBean
    private TariffRepository tariffRepository;

    @MockBean
    private PaymentServiceFeignClient paymentServiceFeignClient;

    @Autowired
    private UserServiceProperties userServiceProperties;

    @Test
    void buyTariffSuccess() {
        long userId = 1L;
        TariffDto dto = userServiceProperties.getListAvailableTariffDtos().get(0);

        when(paymentServiceFeignClient.sendPayment(any()))
                .thenReturn(new ResponseEntity<>(new PaymentResponse(1111L,
                                PaymentStatus.SUCCESS,
                                1,
                                1L,
                                BigDecimal.valueOf(100),
                                Currency.USD,
                                "message"), HttpStatus.OK)
                        );

        Tariff.TariffBuilder tariff = Tariff.builder()
                .plan("super-user")
                .shows(100)
                .priority(1)
                .isActive(true)
                .expirePeriod(LocalDateTime.now())
                .paymentId(1111L)
                .user(User.builder().id(userId).build());

        when(tariffRepository.save(any())).thenReturn(tariff.build());

        dto.setUserId(userId);
        tariffService.buyTariff(dto, userId);

        verify(tariffRepository).save(tariff.expirePeriod(any()).build());
    }
}