package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.UserDto;
import school.faang.user_service.entity.promotion.user.UserPromotion;
import school.faang.user_service.exception.DuplicatePromotionException;
import school.faang.user_service.exception.PromotionNotFoundException;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.user.UserPromotionType;
import school.faang.user_service.repository.promotion.UserPromotionCountRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.PromotionService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.PromotionService.DUPLICATE_USER_PROMOTION_MESSAGE;
import static school.faang.user_service.service.PromotionService.NO_USER_PROMOTION_FOUND;
import static school.faang.user_service.service.PromotionService.USER_DTO_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.DATE_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.PROMOTION_PRIORITY_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.START_DATE_CANNOT_BE_AFTER_END_DATE;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.USER_ID_CANNOT_BE_NULL;
import static school.faang.user_service.utils.validatonUtils.PromotionValidation.USER_PROMOTION_TYPE_CANNOT_BE_NULL;

@ExtendWith(MockitoExtension.class)
public class UserPromotionTest {
    @Mock
    private UserPromotionRepository userPromotionRepository;

    @Mock
    private UserPromotionCountRepository userPromotionCountRepository;

    @InjectMocks
    private PromotionService promotionService;

    private final LocalDateTime startDate = LocalDateTime.now();
    private final LocalDateTime endDate = LocalDateTime.now().plusMonths(2);
    private final UserPromotionType userPromotionType = UserPromotionType.TEN_PERCENT_OF_USERS;
    private final PromotionPriority promotionPriority = PromotionPriority.PRIORITY_MEDIUM;
    private final UserDto userDto = new UserDto(1L, 1L, "name", "city");

    @Test
    public void testStartPromotion_nullUserDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.startUserPromotion(null, startDate, endDate,
                        userPromotionType, promotionPriority)
        );
        assertEquals(USER_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }


    @Test
    public void testStartPromotion_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.startUserPromotion(userDto, null, endDate,
                        userPromotionType, promotionPriority)
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testStartPromotion_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.startUserPromotion(userDto, LocalDateTime.now().plusMinutes(1), LocalDateTime.now(),
                        userPromotionType, promotionPriority)
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testStartPromotion_nullUserid() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.startUserPromotion(new UserDto(1L, null, "name", "city"),
                        startDate, endDate, userPromotionType, promotionPriority)
        );
        assertEquals(USER_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testStartPromotion_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.startUserPromotion(userDto, startDate, endDate, null, promotionPriority)
        );
        assertEquals(USER_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testStartPromotion_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.startUserPromotion(userDto, startDate, endDate, userPromotionType, null)
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testStartPromotion_callFindPromotionByUserIdStartDateEndDate() {
        when(userPromotionRepository.findPromotionByUserIdStartDateEndDate(anyLong(), any(), any()))
                .thenReturn(null);

        promotionService.startUserPromotion(userDto, startDate, endDate, userPromotionType, promotionPriority);

        verify(userPromotionRepository, times(1))
                .findPromotionByUserIdStartDateEndDate(anyLong(), any(), any());
    }

    @Test
    public void testStartPromotion_duplicatePromotion() {
        when(userPromotionRepository.findPromotionByUserIdStartDateEndDate(anyLong(), any(), any()))
                .thenReturn(new UserPromotion());
        DuplicatePromotionException duplicatePromotionException = assertThrows(
                DuplicatePromotionException.class,
                () -> promotionService.startUserPromotion(userDto, startDate, endDate,
                        userPromotionType, promotionPriority)
        );

        assertEquals(String.format(DUPLICATE_USER_PROMOTION_MESSAGE, userDto.userId(), startDate, endDate),
                duplicatePromotionException.getMessage());
    }


    @Test
    public void testStartPromotion_savePromotion() {
        when(userPromotionRepository.findPromotionByUserIdStartDateEndDate(anyLong(), any(), any()))
                .thenReturn(null);

        promotionService.startUserPromotion(userDto, startDate, endDate, userPromotionType, promotionPriority);

        ArgumentCaptor<UserPromotion> captor = ArgumentCaptor.forClass(UserPromotion.class);
        verify(userPromotionRepository, times(1))
                .save(captor.capture());

        UserPromotion savedPromotion = captor.getValue();
        assertEquals(userDto.userId(), savedPromotion.getUserId());
        assertEquals(startDate, savedPromotion.getStartDate());
        assertEquals(endDate, savedPromotion.getEndDate());
        assertEquals(userPromotionType.getUserPercentage(), savedPromotion.getPercentage());
        assertEquals(promotionPriority.getFeedRank(), savedPromotion.getFeedRank());
    }

    @Test
    public void testEndPromotion_nullUserDto() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.endUserPromotion(null, startDate, endDate,
                        userPromotionType, promotionPriority)
        );
        assertEquals(USER_DTO_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testEndPromotion_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.endUserPromotion(userDto, startDate, null,
                        userPromotionType, promotionPriority)
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testEndPromotion_nullUserid() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.endUserPromotion(new UserDto(1L, null, "name", "city"),
                        startDate, endDate, userPromotionType, promotionPriority)
        );
        assertEquals(USER_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testEndPromotion_startDateAfterEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.endUserPromotion(userDto, LocalDateTime.now().plusMinutes(1), LocalDateTime.now(),
                        userPromotionType, promotionPriority)
        );
        assertEquals(START_DATE_CANNOT_BE_AFTER_END_DATE, illegalArgumentException.getMessage());
    }

    @Test
    public void testEndPromotion_nullPromotionType() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.endUserPromotion(userDto, startDate, endDate, null, promotionPriority)
        );
        assertEquals(USER_PROMOTION_TYPE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testEndPromotion_nullPromotionPriority() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.endUserPromotion(userDto, startDate, endDate, userPromotionType, null)
        );
        assertEquals(PROMOTION_PRIORITY_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testEndPromotion_callSamePromotion() {
        when(userPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new UserPromotion());

        promotionService.endUserPromotion(userDto, startDate, endDate, userPromotionType, promotionPriority);

        verify(userPromotionRepository, times(1))
                .findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt());
    }

    @Test
    public void testEndPromotion_noPromotionFound() {
        when(userPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(null);

        PromotionNotFoundException promotionNotFoundException = assertThrows(PromotionNotFoundException.class, () ->
                promotionService.endUserPromotion(userDto, startDate, endDate, userPromotionType, promotionPriority)
        );

        assertEquals(String.format(NO_USER_PROMOTION_FOUND, userDto.userId(), startDate,
                        endDate, userPromotionType.getUserPercentage(), promotionPriority.getFeedRank()),
                promotionNotFoundException.getMessage());
    }

    @Test
    public void testEndPromotion_delete() {
        Long userId = 1L;
        when(userPromotionRepository.findSamePromotion(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new UserPromotion(userId, startDate, endDate, userDto.userId(),
                        userPromotionType.getUserPercentage(), promotionPriority.getFeedRank()));

        promotionService.endUserPromotion(userDto, startDate, endDate, userPromotionType, promotionPriority);

        ArgumentCaptor<UserPromotion> captor = ArgumentCaptor.forClass(UserPromotion.class);
        verify(userPromotionRepository, times(1))
                .delete(captor.capture());

        UserPromotion deletedPromotion = captor.getValue();
        assertEquals(userId, deletedPromotion.getUserId());
        assertEquals(startDate, deletedPromotion.getStartDate());
        assertEquals(endDate, deletedPromotion.getEndDate());
        assertEquals(userPromotionType.getUserPercentage(), deletedPromotion.getPercentage());
        assertEquals(promotionPriority.getFeedRank(), deletedPromotion.getFeedRank());
    }

    @Test
    public void testCalculatePromotionPrice_nullId() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.calculateUserPromotionPrice(null, startDate, endDate,
                        userPromotionType.getUserPercentage(), promotionPriority.getFeedRank())
        );
        assertEquals(USER_ID_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testCalculatePromotionPrice_nullStartDateEndDate() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.calculateUserPromotionPrice(userDto.userId(), null, endDate,
                        userPromotionType.getUserPercentage(), promotionPriority.getFeedRank())
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());

        illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                promotionService.calculateUserPromotionPrice(userDto.userId(), startDate, null,
                        userPromotionType.getUserPercentage(), promotionPriority.getFeedRank())
        );
        assertEquals(DATE_CANNOT_BE_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void testCalculatePromotionPrice_callFindCountByUserId() {
        when(userPromotionCountRepository.findCountByUserId(anyLong()))
                .thenReturn(1);

        promotionService.calculateUserPromotionPrice(userDto.userId(), startDate, endDate,
                userPromotionType.getUserPercentage(), promotionPriority.getFeedRank());

        verify(userPromotionCountRepository, times(1))
                .findCountByUserId(userDto.userId());
    }

    @Test
    public void testCalculatePromotionPrice_resultNotNull() {
        assertNotNull(promotionService.calculateUserPromotionPrice(userDto.userId(), startDate, endDate,
                userPromotionType.getUserPercentage(), promotionPriority.getFeedRank()));
    }
}
