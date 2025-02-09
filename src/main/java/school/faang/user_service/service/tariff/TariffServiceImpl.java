package school.faang.user_service.service.tariff;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.payment.PaymentRequest;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.client.payment.PaymentServiceFeignClient;
import school.faang.user_service.common.Currency;
import school.faang.user_service.common.PaymentStatus;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.exception.PaymentException;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.repository.TariffRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {
    private final UserServiceProperties userServiceProperties;
    private final PaymentServiceFeignClient paymentServiceFeignClient;
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final UserContext userContext;

    @Override
    @Transactional
    public Tariff buyTariff(TariffDto tariffDto, Long userId) {
        if (!userServiceProperties.getListAvailableTariffDtos().contains(tariffDto)) {
            String message = String.format("Tariff %s not found", tariffDto);
            log.error(message);
            throw new EntityNotFoundException(message);
        }

        UserServiceProperties.TariffProperties properties = userServiceProperties
                .getAvailableTariffs()
                .get(tariffDto.getPlan());

        sendPayment(tariffDto, properties.getPrice(), Currency.valueOf(properties.getCurrency()), userId);

        tariffDto.setExpirePeriod(LocalDateTime.now().plusDays(properties.getDays()));
        tariffDto.setIsActive(true);
        return tariffRepository.save(tariffMapper.toEntity(tariffDto));
    }

    @Override
    public void decrementShows(Tariff tariff) {
        if (tariff == null || !tariffRepository.existsById(tariff.getId())) {
            return;
        }

        if (tariff.getShows() != null
                && tariff.getShows() > 0
                && tariff.getPriority() != null) {
            tariff.setShows(tariff.getShows() - 1);

            if (tariff.getShows() == 0) {
                tariff.setIsActive(false);
            }
        }

        tariffRepository.save(tariff);
    }

    private void sendPayment(@NonNull TariffDto tariffDto, BigDecimal amount, Currency currency, Long userId) {
        log.info("Start sendPayment, amount: {}, currency: {}", amount, currency);
        if (userId == null) {
            throw new IllegalArgumentException("User id is null");
        }

        PaymentResponse response;
        try {
            response = paymentServiceFeignClient.sendPayment(
                    new PaymentRequest(
                    new Random().nextLong(),
                    amount,
                    currency)).getBody();
        } catch (Exception e) {
            throw new PaymentException(e.getMessage());
        }

        if (response == null || !response.status().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentException("Payment failed, response: " + response);
        }
        tariffDto.setPaymentId(response.id());
        log.info("Payment successfully sent, paymentId: {}", tariffDto.getPaymentId());
    }
}
