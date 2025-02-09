package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentRequest;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.client.payment.PaymentServiceFeignClient;
import school.faang.user_service.common.PaymentStatus;
import school.faang.user_service.common.PremiumPeriod;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PremiumInvalidDataException;
import school.faang.user_service.exception.PremiumNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final UserRepository userRepository;
    private final PaymentServiceFeignClient paymentServiceClient;
    private final UserContext userContext;

    @Override
    public PremiumDto buyPremium(Integer days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        User user = validateAndGetUser(userContext.getUserId());
        PaymentRequest paymentRequest = createPaymentRequest(premiumPeriod);
        PaymentResponse paymentResponse = sendPaymentRequest(paymentRequest);

        if (paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            Premium premium = savePremium(premiumPeriod, user);
            return premiumMapper.toDto(premium);
        }
        throw new PremiumInvalidDataException(String.format("Error from paymentService: %s", paymentResponse.message()));
    }

    private User validateAndGetUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PremiumNotFoundException(String.format("No user found by this userId: %s", userId)));
        if (premiumRepository.existsByUserId(user.getId())) {
            throw new PremiumInvalidDataException(String.format("User with id: %s already has a Premium", userId));
        }
        return user;
    }

    private PaymentRequest createPaymentRequest(PremiumPeriod premiumPeriod) {
        return PaymentRequest.builder()
                .paymentNumber(Instant.now().toEpochMilli())
                .amount(premiumPeriod.getPrice())
                .currency(premiumPeriod.getCurrency())
                .build();
    }

    private PaymentResponse sendPaymentRequest(PaymentRequest paymentRequest) {
        return paymentServiceClient.sendPayment(paymentRequest).getBody();
    }

    private Premium savePremium(PremiumPeriod premiumPeriod, User user) {
        LocalDateTime now = LocalDateTime.now();
        return premiumRepository.save(Premium.builder()
                .user(user)
                .startDate(now)
                .endDate(now.plusDays(premiumPeriod.getDays()))
                .build());
    }
}
