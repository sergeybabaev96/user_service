package school.faang.user_service.service.promotion.user;

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
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.exception.PromotionNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.UserPromotionService.CANT_UPDATE_USER_PROMOTION_PRIORITY;
import static school.faang.user_service.service.UserPromotionService.USER_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.USER_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.USER_PROMOTION_TYPE_CANNOT_BE_NULL;

@ExtendWith(MockitoExtension.class)
public class UpdateUserPromotionPriorityTest {
    @Mock
    private UserPromotionRepository userPromotionRepository;

    @InjectMocks
    private UserPromotionService userPromotionService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserPromotionCountRepository userPromotionCountRepository;

    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final UserPromotionType userPromotionType = UserPromotionType.TEN_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final UserDto userDto = new UserDto(1L, 1L, "name", "city");
    private final PaymentResponse successResponse = new PaymentResponse(PaymentStatus.SUCCESS, 1,
            2L, BigDecimal.ONE, Currency.USD, "message");

    @Test
    public void testProcessUpdateUserPromotionPriority_nullUserDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(null, startDate, endDate,
                        userPromotionType, promotionPriority)
        );
        assertEquals(USER_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(userDto, null, null,
                        userPromotionType, promotionPriority)
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(userDto, LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now(), userPromotionType, promotionPriority)
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_nullUserId() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(new UserDto(1L, null,
                        "name", "city"), startDate, endDate, userPromotionType, promotionPriority)
        );
        assertEquals(USER_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(userDto, startDate, endDate,
                        null, promotionPriority)
        );
        assertEquals(USER_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(userDto, startDate, endDate,
                        userPromotionType, null)
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_noPromotion() {
        when(userPromotionRepository.getUserFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(null);

        PromotionNotFoundException promotionNotFoundException = assertThrows(PromotionNotFoundException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(userDto, startDate, endDate,
                        userPromotionType, promotionPriority)
        );

        assertEquals(String.format(CANT_UPDATE_USER_PROMOTION_PRIORITY + ". No such promotion exists",
                userDto.userId(), startDate, endDate, userPromotionType), promotionNotFoundException.getMessage());
        verify(userPromotionRepository, times(1)).getUserFeedRank(userDto.userId(),
                startDate, endDate, userPromotionType.getUserPercentage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_noChanges() {
        when(userPromotionRepository.getUserFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(250);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                userPromotionService.processUpdateUserPromotionPriority(userDto, startDate, endDate,
                        userPromotionType, promotionPriority)
        );

        assertEquals(String.format(CANT_UPDATE_USER_PROMOTION_PRIORITY + ". Such promotion already exists",
                userDto.userId(), startDate, endDate, userPromotionType), illegalArgumentException.getMessage());
        verify(userPromotionRepository, times(1)).getUserFeedRank(userDto.userId(),
                startDate, endDate, userPromotionType.getUserPercentage());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_successfulPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(successResponse);

        when(userPromotionRepository.getUserFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(1000);
        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionPriority(userDto,
                startDate, endDate, userPromotionType, promotionPriority
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User promotion priority updated successfully", response.getBody());
    }

    @Test
    public void testProcessUpdateUserPromotionPriority_failedPayment() {
        when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class)))
                .thenReturn(null);

        when(userPromotionRepository.getUserFeedRank(anyLong(), any(), any(), anyInt()))
                .thenReturn(1000);
        ResponseEntity<String> response = userPromotionService.processUpdateUserPromotionPriority(userDto,
                startDate, endDate, userPromotionType, promotionPriority
        );

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Payment failed for promotion priority update", response.getBody());
    }
}
