package school.faang.user_service.service.promotion.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.entity.promotion.user.UserPromotion;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.user.UserPromotionType;
import school.faang.user_service.repository.promotion.UserPromotionCountRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.UserPromotionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.UserPromotionService.USER_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.USER_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.USER_PROMOTION_TYPE_CANNOT_BE_NULL;

@ExtendWith(MockitoExtension.class)
public class StartUserPromotionTest {
    @Mock
    private UserPromotionRepository userPromotionRepository;

    @Mock
    private UserPromotionCountRepository userPromotionCountRepository;

    @InjectMocks
    private UserPromotionService userPromotionService;

    @Mock
    private RestTemplate restTemplate;

    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final UserPromotionType userPromotionType = UserPromotionType.TEN_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final UserDto userDto = new UserDto(1L, 1L, "name", "city");
    private final PaymentResponse successResponse = new PaymentResponse(PaymentStatus.SUCCESS, 1, 2L,
        BigDecimal.ONE, Currency.USD, "message");

    @Test
    public void testProcessStartUserPromotion_nullUserDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processStartUserPromotion(null, startDate, endDate,
                        userPromotionType, promotionPriority)
        );
        assertEquals(USER_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartUserPromotion_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processStartUserPromotion(userDto, null, endDate,
                        userPromotionType, promotionPriority)
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartUserPromotion_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processStartUserPromotion(userDto, LocalDateTime.now().plusMinutes(1), LocalDateTime.now(),
                        userPromotionType, promotionPriority)
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartUserPromotion_nullUserid() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processStartUserPromotion(new UserDto(1L, null, "name", "city"),
                        startDate, endDate, userPromotionType, promotionPriority)
        );
        assertEquals(USER_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartUserPromotion_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processStartUserPromotion(userDto, startDate, endDate, null, promotionPriority)
        );
        assertEquals(USER_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartUserPromotion_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processStartUserPromotion(userDto, startDate, endDate, userPromotionType, null)
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessStartUserPromotion_callFindCountByUserIdAndSaveCount() {
        when(userPromotionCountRepository.save(any())).thenReturn(null);
        when(userPromotionCountRepository.findCountByUserId(anyLong())).thenReturn(null);
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(successResponse);

        userPromotionService.processStartUserPromotion(userDto, startDate, endDate,
                userPromotionType, promotionPriority);

        verify(userPromotionCountRepository, times(2))
                .findCountByUserId(anyLong());
        verify(userPromotionCountRepository, times(1))
                .save(any());
    }

    @Test
    public void testProcessStartUserPromotion_saveUserPromotion() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(successResponse);
        when(userPromotionCountRepository.findCountByUserId(anyLong())).thenReturn(null);

        userPromotionService.processStartUserPromotion(userDto, startDate, endDate,
                userPromotionType, promotionPriority);

        ArgumentCaptor<UserPromotion> captor = ArgumentCaptor.forClass(UserPromotion.class);
        verify(userPromotionRepository).save(captor.capture());

        UserPromotion capturedUserPromotion = captor.getValue();
        assertEquals(userDto.userId(), capturedUserPromotion.getUserId());
        assertEquals(userPromotionType.getUserPercentage(), capturedUserPromotion.getPercentage());
        assertEquals(startDate, capturedUserPromotion.getStartDate());
        assertEquals(endDate, capturedUserPromotion.getEndDate());
        assertEquals(promotionPriority.getFeedRank(), capturedUserPromotion.getFeedRank());
    }

    @Test
    public void testProcessStartUserPromotion_successfulPayment() {

        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(successResponse);

        ResponseEntity<String> response = userPromotionService.processStartUserPromotion(userDto,
                startDate, endDate, userPromotionType, promotionPriority
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User promotion started successfully", response.getBody());
    }

    @Test
    public void testProcessStartUserPromotion_failedPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(null);

        ResponseEntity<String> response = userPromotionService.processStartUserPromotion(userDto,
                startDate, endDate, userPromotionType, promotionPriority
        );

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Payment failed for user promotion", response.getBody());
    }
}
