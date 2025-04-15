package school.faang.user_service.service.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.exchange.ExchangeRequestDto;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumAnalyticsDto;
import school.faang.user_service.dto.premium.PremiumNotificationDto;
import school.faang.user_service.dto.premium.PremiumPaymentRequestDto;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.exception.premium.PremiumAlreadyPurchasedException;
import school.faang.user_service.exception.premium.PremiumNotActiveException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.kafka.publisher.KafkaPublisher;
import school.faang.user_service.service.premium.PremiumServiceImpl;
import school.faang.user_service.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.messages.ErrorMessages.NO_ACTIVE_PREMIUM;
import static school.faang.user_service.messages.ErrorMessages.PREMIUM_HAS_ALREADY_BEEN_PURCHASED;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    @InjectMocks
    private PremiumServiceImpl premiumService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumRepository premiumRepository;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    private KafkaPublisher kafkaPublisher;

    @Mock
    private ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    @Mock
    private JsonUtils jsonUtils;

    @Mock
    private RequestReplyFuture<String, String, String> requestReplyFuture;

    private final Country country = new Country(1L, "Title", Collections.emptyList());
    private PremiumRequestDto premiumRequestDto;
    private User user;
    private PremiumPaymentResponseDto premiumPaymentResponse;
    private ExchangeResponseDto exchangeResponse;
    private PaymentResponseDto paymentResponseDto;
    private ConsumerRecord<String, String> consumerRecord;

    private final String jsonRequest = "jsonRequest";
    private final String jsonResponse = "jsonResponse";
    private final String premiumBoughtTopic = "topic1";
    private final String premiumExpiredTopic = "topic2";
    private final String premiumExpireSoonTopic = "topic3";
    private final String premiumAutoRenewFailedTopic = "topic4";
    private final String premiumUpdatedTopic = "topic5";
    private final String premiumAnalyticsTopic = "topic6";
    private final String premiumPaymentFailedTopic = "topic7";
    private final String premiumPaymentRequestTopic = "topic8";
    private final String premiumPriceRequestTopic = "topic9";
    private final String premiumPriceCorrelationId = "correlation-id-1";
    private final String premiumPaymentCorrelationId = "correlation-id-2";

    @BeforeEach
    public void setUp() {
        paymentResponseDto = new PaymentResponseDto(PaymentStatus.SUCCESS, 1, 1L,
                BigDecimal.TEN, CurrencyDto.USD, "message");
        premiumRequestDto = new PremiumRequestDto(PremiumType.ONE_MONTH, 1L, CurrencyDto.USD, true);
        user = User.builder().id(1L).username("name").country(country).build();
        premiumPaymentResponse = new PremiumPaymentResponseDto(premiumRequestDto, paymentResponseDto, true);
        consumerRecord = new ConsumerRecord<>("topic", 0, 0L, "key", jsonResponse);
        exchangeResponse = new ExchangeResponseDto(CurrencyDto.USD, BigDecimal.TEN, 1L);
        requestReplyFuture = new RequestReplyFuture<>();
        requestReplyFuture.complete(consumerRecord);

        ReflectionTestUtils.setField(premiumService, "premiumBoughtTopic", premiumBoughtTopic);
        ReflectionTestUtils.setField(premiumService, "premiumExpiredTopic", premiumExpiredTopic);
        ReflectionTestUtils.setField(premiumService, "premiumExpireSoonTopic", premiumExpireSoonTopic);
        ReflectionTestUtils.setField(premiumService, "premiumAutoRenewFailedTopic", premiumAutoRenewFailedTopic);
        ReflectionTestUtils.setField(premiumService, "premiumUpdatedTopic", premiumUpdatedTopic);
        ReflectionTestUtils.setField(premiumService, "premiumAnalyticsTopic", premiumAnalyticsTopic);
        ReflectionTestUtils.setField(premiumService, "premiumPaymentFailedTopic", premiumPaymentFailedTopic);
        ReflectionTestUtils.setField(premiumService, "premiumPaymentRequestTopic", premiumPaymentRequestTopic);
        ReflectionTestUtils.setField(premiumService, "premiumPriceRequestTopic", premiumPriceRequestTopic);

        ReflectionTestUtils.setField(premiumService, "premiumPriceCorrelationId", premiumPriceCorrelationId);
        ReflectionTestUtils.setField(premiumService, "premiumPaymentCorrelationId", premiumPaymentCorrelationId);

        ReflectionTestUtils.setField(premiumService, "renewThreadPoolSize", 2);
        ReflectionTestUtils.setField(premiumService, "renewBatchSize", 100);
        ReflectionTestUtils.setField(premiumService, "premiumRenewalTimeoutHours", 1);
        ReflectionTestUtils.setField(premiumService, "premiumExpiryNotificationDays", 5);

        premiumService.setUp();
    }

    @Test
    public void testBuyPremium_purchasedByUser() throws Exception {
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));
        when(jsonUtils.serialize(any(PremiumPaymentRequestDto.class))).thenReturn(jsonRequest);
        when(jsonUtils.deserialize(anyString(), eq(PremiumPaymentResponseDto.class)))
                .thenReturn(premiumPaymentResponse);

        when(replyingKafkaTemplate.sendAndReceive(Mockito.<ProducerRecord<String, String>>any()))
                .thenReturn(requestReplyFuture);

        premiumService.buyPremium(premiumRequestDto, true);

        verify(jsonUtils, times(1)).serialize(any(PremiumPaymentRequestDto.class));
        verify(jsonUtils, times(1)).deserialize(jsonResponse, PremiumPaymentResponseDto.class);
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumAnalyticsDto.class), eq(premiumAnalyticsTopic));
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumBoughtTopic));
        verify(premiumRepository, times(1)).save(any(Premium.class));
    }

    @Test
    public void testBuyPremium_userAlreadyHasPremium() throws Exception {
        user.setPremium(Premium.builder().endDate(LocalDateTime.now().minusDays(1)).build());
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));

        PremiumAlreadyPurchasedException exception = assertThrows(PremiumAlreadyPurchasedException.class,
                () -> premiumService.buyPremium(premiumRequestDto, true)
        );

        assertEquals(PREMIUM_HAS_ALREADY_BEEN_PURCHASED.formatted(user.getId()), exception.getMessage());
    }

    @Test
    public void testBuyPremium_userNotFound() throws Exception {
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> premiumService.buyPremium(premiumRequestDto, true)
        );

        assertEquals("No user with ID " + premiumRequestDto.getUserId(), exception.getMessage());
    }

    @Test
    public void testBuyPremium_purchasedAutomatically() throws Exception {
        premiumPaymentResponse.setByUser(false);
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));
        when(jsonUtils.serialize(any(PremiumPaymentRequestDto.class))).thenReturn(jsonRequest);
        when(jsonUtils.deserialize(anyString(), eq(PremiumPaymentResponseDto.class)))
                .thenReturn(premiumPaymentResponse);

        when(replyingKafkaTemplate.sendAndReceive(Mockito.<ProducerRecord<String, String>>any()))
                .thenReturn(requestReplyFuture);

        premiumService.buyPremium(premiumRequestDto, false);

        verify(jsonUtils, times(1)).serialize(any(PremiumPaymentRequestDto.class));
        verify(jsonUtils, times(1)).deserialize(jsonResponse, PremiumPaymentResponseDto.class);
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumAnalyticsDto.class), eq(premiumAnalyticsTopic));
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumUpdatedTopic));
        verify(premiumRepository, times(1)).save(any(Premium.class));
    }

    @Test
    public void testGetPremiumPrice_success() {
        when(jsonUtils.serialize(any(ExchangeRequestDto.class))).thenReturn(jsonRequest);
        when(jsonUtils.deserialize(anyString(), eq(ExchangeResponseDto.class)))
                .thenReturn(exchangeResponse);

        when(replyingKafkaTemplate.sendAndReceive(Mockito.<ProducerRecord<String, String>>any()))
                .thenReturn(requestReplyFuture);

        premiumService.getPremiumPrice(premiumRequestDto);

        verify(jsonUtils, times(1)).serialize(any(ExchangeRequestDto.class));
        verify(jsonUtils, times(1)).deserialize(jsonResponse, ExchangeResponseDto.class);
    }

    @Test
    public void testUpdateAutoRenew_userNotFound() {
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> premiumService.updateAutoRenew(true, premiumRequestDto.getUserId())
        );

        assertEquals("No user with ID " + premiumRequestDto.getUserId(), exception.getMessage());
    }

    @Test
    public void testUpdateAutoRenew_noActivePremium() {
        user.setPremium(null);
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));

        PremiumNotActiveException exception = assertThrows(PremiumNotActiveException.class,
                () -> premiumService.updateAutoRenew(true, premiumRequestDto.getUserId())
        );

        assertEquals(NO_ACTIVE_PREMIUM.formatted(premiumRequestDto.getUserId()), exception.getMessage());
    }

    @Test
    public void testUpdateAutoRenew_success() {
        user.setPremium(new Premium());
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));

        premiumService.updateAutoRenew(true, premiumRequestDto.getUserId());

        assertTrue(user.getPremium().isAutoRenew());
    }

    @Test
    public void testPremiumRenewal_withoutAutoRenew_expired() {
        when(userRepository.count()).thenReturn(1L);
        List<User> users = new ArrayList<>();
        Premium premium = Premium.builder()
                .id(1L)
                .user(user)
                .endDate(LocalDateTime.now().plusDays(1))
                .autoRenew(false)
                .build();

        user.setPremium(premium);
        users.add(user);
        when(userRepository.findPremiumActiveUsers(any(Pageable.class))).thenReturn(new PageImpl<>(users));

        premiumService.premiumRenewal();

        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumExpiredTopic));
    }

    @Test
    public void testPremiumRenewal_withoutAutoRenew_expireSoon() {
        when(userRepository.count()).thenReturn(1L);
        List<User> users = new ArrayList<>();
        Premium premium = Premium.builder()
                .id(1L)
                .user(user)
                .endDate(LocalDateTime.now().minusMinutes(1))
                .autoRenew(false)
                .build();

        user.setPremium(premium);
        users.add(user);
        when(userRepository.findPremiumActiveUsers(any(Pageable.class))).thenReturn(new PageImpl<>(users));

        premiumService.premiumRenewal();

        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumExpireSoonTopic));
    }

    @Test
    public void testPremiumRenewal_withAutoRenew() {
        when(userRepository.count()).thenReturn(1L);
        List<User> users = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.now();
        Premium premium = Premium.builder()
                .id(1L)
                .user(user)
                .startDate(startDate)
                .endDate(startDate.plusMonths(3))
                .autoRenew(true)
                .build();

        user.setPremium(premium);
        users.add(user);
        when(userRepository.findPremiumActiveUsers(any(Pageable.class))).thenReturn(new PageImpl<>(users));

        premiumPaymentResponse.setByUser(false);
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));
        when(jsonUtils.serialize(any(PremiumPaymentRequestDto.class))).thenReturn(jsonRequest);
        when(jsonUtils.deserialize(anyString(), eq(PremiumPaymentResponseDto.class)))
                .thenReturn(premiumPaymentResponse);

        when(replyingKafkaTemplate.sendAndReceive(Mockito.<ProducerRecord<String, String>>any()))
                .thenReturn(requestReplyFuture);

        premiumService.premiumRenewal();

        verify(jsonUtils, times(1)).serialize(any(PremiumPaymentRequestDto.class));
        verify(jsonUtils, times(1)).deserialize(jsonResponse, PremiumPaymentResponseDto.class);
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumAnalyticsDto.class), eq(premiumAnalyticsTopic));
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumUpdatedTopic));
        verify(premiumRepository, times(1)).save(any(Premium.class));
    }
}
