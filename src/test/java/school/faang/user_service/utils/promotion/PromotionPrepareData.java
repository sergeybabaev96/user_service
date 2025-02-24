package school.faang.user_service.utils.promotion;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPayment;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.enums.promotion.PromotionTariff;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class PromotionPrepareData {
    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String PLAN_NAME = "BASIC";

    public static PromotionPayment getPromotionPayment() {
        return PromotionPayment.builder()
                .id(RANDOM_UUID)
                .userId(1L)
                .build();
    }

    public static PromotionPaymentDto getPromotionPaymentDtoWithStatus() {
        return PromotionPaymentDto.builder()
                .id(RANDOM_UUID)
                .userId(1L)
                .amount(new BigDecimal(100))
                .currency(Currency.EUR)
                .status(PromotionPaymentStatus.ACCEPTED)
                .build();
    }

    public static PromotionPaymentDto getPromotionPaymentDto() {
        return PromotionPaymentDto.builder()
                .id(RANDOM_UUID)
                .userId(1L)
                .build();
    }

    public static PromotionPaymentDto getPromotionPaymentDtoWhenDeclined() {
        return PromotionPaymentDto.builder()
                .id(RANDOM_UUID)
                .userId(1L)
                .amount(new BigDecimal(100))
                .currency(Currency.EUR)
                .status(PromotionPaymentStatus.DECLINED)
                .build();
    }

    public static PromotionPlanDto getPromotionPlanDto() {
        return PromotionPlanDto.builder()
                .name(PLAN_NAME)
                .build();
    }

    public static PromotionPlanDto getPromotionPlanEventDto() {
        return PromotionPlanDto.builder()
                .name("BASIC")
                .price(new BigDecimal(25))
                .viewsCount(100)
                .build();
    }

    public static PromotionPlan getPromotionPlan() {
        return PromotionPlan.builder()
                .name(PLAN_NAME)
                .build();
    }

    public static PromotionRequestDto getPromotionRequestDto() {
        return PromotionRequestDto.builder()
                .userId(1L)
                .amount(new BigDecimal(100))
                .currency(Currency.EUR)
                .planType(PromotionPlanType.EVENT)
                .tariff(PromotionTariff.BASIC)
                .build();
    }
    public static PromotionRequestDto getPromotionRequestDtoWithEvent() {
        return PromotionRequestDto.builder()
                .userId(1L)
                .eventId(1L)
                .amount(new BigDecimal(100))
                .currency(Currency.EUR)
                .planType(PromotionPlanType.EVENT)
                .tariff(PromotionTariff.BASIC)
                .build();
    }

    public static Promotion getPromotionWithEvent() {
        return Promotion
                .builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .event(Event.builder().id(1L).build())
                .build();
    }

    public static Promotion getPromotionWithEventWhenInactive() {
        return Promotion
                .builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .event(Event.builder().id(1L).build())
                .status(PromotionStatus.INACTIVE)
                .promotionPayment(PromotionPayment.builder()
                        .id(RANDOM_UUID)
                        .build())
                .build();
    }

    public static PromotionResponseDto getPromotionResponseDto() {
        return PromotionResponseDto.builder()
                .userId(1L)
                .paymentId(RANDOM_UUID)
                .tariff(PromotionTariff.PREMIUM)
                .planType(PromotionPlanType.USER)
                .status(PromotionStatus.ACTIVE)
                .usedViews(100)
                .build();
    }

    public static PromotionResponseDto getPromotionResponseDtoWhenDeclined() {
        return PromotionResponseDto.builder()
                .userId(1L)
                .eventId(1L)
                .paymentId(RANDOM_UUID)
                .status(PromotionStatus.INACTIVE)
                .build();
    }

    public static Promotion getUserPromotion() {
        return Promotion.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .username("user")
                        .email("email")
                        .phone("phone")
                        .build())
                .promotionPayment(PromotionPayment.builder()
                        .id(RANDOM_UUID)
                        .build())
                .status(PromotionStatus.ACTIVE)
                .usedViews(100)
                .tariff(PromotionTariff.PREMIUM)
                .planType(PromotionPlanType.USER)
                .build();
    }

    public static Promotion getEventPromotion() {
        return Promotion.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .username("user")
                        .email("email")
                        .phone("phone")
                        .participatedEvents(List.of(Event.builder().id(1L).build()))
                        .build())
                .event(Event.builder()
                        .id(1L)
                        .title("event")
                        .build())
                .promotionPayment(PromotionPayment.builder()
                        .id(RANDOM_UUID)
                        .build())
                .status(PromotionStatus.ACTIVE)
                .usedViews(100)
                .tariff(PromotionTariff.PREMIUM)
                .planType(PromotionPlanType.USER)
                .build();
    }

    public static User getUserWithEvent() {
        return User.builder()
                .ownedEvents(List.of(Event.builder()
                        .id(1L)
                        .build()))
                .build();
    }

    public static User getUser() {
        return User.builder()
                .id(1L)
                .username("user")
                .build();
    }

    public static UserDto getUserDto() {
        return new UserDto(1L, "user", "email", "phone", null);
    }

    public static Event getEvent() {
        return Event.builder()
                .id(1L)
                .build();
    }

    public static Event getEventWithSecondId() {
        return Event.builder()
                .id(2L)
                .build();
    }

    public static PromotionResponseDto getPromotionResponseWithEventDto() {
        return PromotionResponseDto.builder()
                .userId(1L)
                .eventId(1L)
                .build();
    }

    public static PromotionPlan getPromotionPlanPremium() {
        return PromotionPlan.builder()
                .name("PREMIUM")
                .viewsCount(120)
                .build();
    }
}
