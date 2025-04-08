package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.exchange.ExchangeRequestDto;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PaymentServiceClient paymentServiceClient;
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;

    @Override
    @Transactional
    public ResponseEntity<PremiumResponseDto> buyPremium(PremiumRequestDto premiumRequestDto) {
        BigDecimal amount = getPremiumPrice(premiumRequestDto).getAmount();
        log.info("Calculated amount for premium type {} is {} {}",
                premiumRequestDto.getPremiumType(), amount, premiumRequestDto.getCurrency());
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(premiumRequestDto.getUserId(),
                amount, premiumRequestDto.getCurrency());

        ResponseEntity<PaymentResponseDto> response = paymentServiceClient.sendPayment(paymentRequestDto);
        if (response.getStatusCode().isError()) {
            log.error("Payment failed with status: {}", response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode())
                    .body(PremiumResponseDto.builder().userId(premiumRequestDto.getUserId()).build());
        }
        User user = getUserById(premiumRequestDto.getUserId());
        user.setPremiumActive(true);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(premiumRequestDto.getPremiumType().getMonths());
        Premium premium = Premium.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .autoRenew(premiumRequestDto.isAutoRenew())
                .build();

        premiumRepository.save(premium);
        PremiumResponseDto premiumResponse = premiumMapper.toPremiumResponseDto(premium);
        premiumResponse.setPremiumType(premiumRequestDto.getPremiumType());
        return ResponseEntity.ok(premiumResponse);
    }

    @Override
    public ExchangeResponseDto getPremiumPrice(PremiumRequestDto premiumRequestDto) {
        ExchangeRequestDto exchangeRequest = new ExchangeRequestDto(CurrencyDto.USD,
                premiumRequestDto.getCurrency(), premiumRequestDto.getPremiumType().getPriceInDollars());
        return null;
    }

    @Override
    public void updateAutoRenew(boolean autoRenew, Long userId) {

    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with ID " + userId));
    }
}
