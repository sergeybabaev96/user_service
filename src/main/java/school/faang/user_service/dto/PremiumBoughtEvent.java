package school.faang.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import school.faang.user_service.common.PremiumPeriod;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.premium.Premium;

@Getter
@Setter
public class PremiumBoughtEvent extends ApplicationEvent {
    private PremiumPeriod premiumPeriod;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private Premium premium;

    public PremiumBoughtEvent(Object source, PremiumPeriod premiumPeriod, PaymentRequest paymentRequest, PaymentResponse paymentResponse, Premium premium) {
        super(source);
        this.premiumPeriod = premiumPeriod;
        this.paymentRequest = paymentRequest;
        this.paymentResponse = paymentResponse;
        this.premium = premium;
    }
}
