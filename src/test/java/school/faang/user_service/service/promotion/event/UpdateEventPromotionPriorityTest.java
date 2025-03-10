package school.faang.user_service.service.promotion.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.EventDto;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.exception.PromotionNotFoundException;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionType;
import school.faang.user_service.repository.promotion.EventPromotionCountRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.service.EventPromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.EventPromotionService.CANT_UPDATE_EVENT_PROMOTION_PRIORITY;
import static school.faang.user_service.service.EventPromotionService.EVENT_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.EVENT_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.EVENT_PROMOTION_TYPE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;

@ExtendWith(MockitoExtension.class)
public class UpdateEventPromotionPriorityTest {
    @Mock
    private EventPromotionRepository eventPromotionRepository;

    @InjectMocks
    private EventPromotionService eventPromotionService;

    @Mock
    private EventPromotionCountRepository eventPromotionCountRepository;

    @Mock
    private RestTemplate restTemplate;

    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final EventPromotionType eventPromotionType = EventPromotionType.TEN_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final EventDto eventDto = new EventDto(1L, 1L, "title", "description",
            startDate, endDate, "location");
    private final PaymentResponse successResponse = new PaymentResponse(PaymentStatus.SUCCESS, 1, 2L,
            BigDecimal.ONE, Currency.USD, "message");
    private final EventPromotion eventPromotion = new EventPromotion(1L, startDate, endDate, eventDto.eventId(),
            eventPromotionType.getUserPercentage(), promotionPriority.getFeedRank());

    @Test
    public void testProcessUpdateEventPromotionPriority_nullEventDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(null, startDate, endDate,
                        eventPromotionType, promotionPriority)
        );
        assertEquals(EVENT_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(eventDto, null, null,
                        eventPromotionType, promotionPriority)
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(eventDto, LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now(), eventPromotionType, promotionPriority)
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_nullEventId() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(new EventDto(1L, null, "title",
                                "description", startDate, endDate, "location"), startDate, endDate,
                        eventPromotionType, promotionPriority)
        );
        assertEquals(EVENT_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(eventDto, startDate, endDate,
                        null, promotionPriority)
        );
        assertEquals(EVENT_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(eventDto, startDate, endDate,
                        eventPromotionType, null)
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_noPromotion() {
        when(eventPromotionRepository.getEventFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(null);

        PromotionNotFoundException promotionNotFoundException = assertThrows(PromotionNotFoundException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(eventDto, startDate, endDate,
                        eventPromotionType, promotionPriority)
        );

        assertEquals(String.format(CANT_UPDATE_EVENT_PROMOTION_PRIORITY + ". No such promotion exists",
                eventDto.eventId(), startDate, endDate, eventPromotionType), promotionNotFoundException.getMessage());
        verify(eventPromotionRepository, times(1)).getEventFeedRank(eventDto.eventId(),
                startDate, endDate, eventPromotionType.getUserPercentage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_noChanges() {
        when(eventPromotionRepository.getEventFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(250);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                eventPromotionService.processUpdateEventPromotionPriority(eventDto, startDate, endDate,
                        eventPromotionType, promotionPriority)
        );

        assertEquals(String.format(CANT_UPDATE_EVENT_PROMOTION_PRIORITY + ". Such promotion already exists",
                eventDto.eventId(), startDate, endDate, eventPromotionType), illegalArgumentException.getMessage());
        verify(eventPromotionRepository, times(1)).getEventFeedRank(eventDto.eventId(),
                startDate, endDate, eventPromotionType.getUserPercentage());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_successfulPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(successResponse);

        when(eventPromotionRepository.getEventFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(1000);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionPriority(eventDto,
                startDate, endDate, eventPromotionType, promotionPriority
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User promotion priority updated successfully", response.getBody());
    }

    @Test
    public void testProcessUpdateEventPromotionPriority_failedPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(null);

        when(eventPromotionRepository.getEventFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(1000);
        ResponseEntity<String> response = eventPromotionService.processUpdateEventPromotionPriority(eventDto,
                startDate, endDate, eventPromotionType, promotionPriority
        );

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Payment failed for promotion priority update", response.getBody());
    }
}
