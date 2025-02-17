package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment_service.PaymentResponse;
import school.faang.user_service.dto.payment_service.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.PremiumException;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.payment.PaymentService;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public Premium buyPremium(Long userId, int days) {
        log.info("User {} trying to buy premium for {} days", userId, days);
        PremiumPeriod period = PremiumPeriod.fromDays(days);
        checkUserDoesntHavePremium(userId);

        PaymentResponse response = paymentService.sendPaymentRequest(period);
        checkResponseStatus(response);

        User user = userService.getUser(userId);
        Premium premium = createPremiumForUser(user, period);

        log.info("Saving new premium for user {} with start date {} and end date {}",
                userId, premium.getStartDate(), premium.getEndDate());
        return premiumRepository.save(premium);
    }

    public boolean checkPremiumByUserId(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return premiumRepository.findByUserIdAndStartDateAfterAndEndDateBefore(userId, now, now).isPresent();
    }

    private void checkUserDoesntHavePremium(Long userId) {
        LocalDateTime currentTime = LocalDateTime.now();

        premiumRepository.findByUserIdAndEndDateAfter(userId, currentTime)
                .ifPresent(premium -> {
                    throw new DataValidationException(
                            String.format("User %d already has a valid premium subscription until %s",
                                    userId, premium.getEndDate()));
                });
    }

    private void checkResponseStatus(PaymentResponse response) {
        if (!Objects.equals(response.status(), PaymentStatus.SUCCESS)) {
            log.error("Payment failed with status: {}", response.status());
            throw new PremiumException("Cannot buy premium, try again later");
        }
    }

    private Premium createPremiumForUser(User user, PremiumPeriod period) {
        LocalDateTime currentTime = LocalDateTime.now();
        return Premium.builder()
                .user(user)
                .startDate(currentTime)
                .endDate(currentTime.plusDays(period.getDays()))
                .build();
    }
}
