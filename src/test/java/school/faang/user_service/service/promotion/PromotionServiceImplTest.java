package school.faang.user_service.service.promotion;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.mapper.promotion.PromotionMapperImpl;
import school.faang.user_service.mapper.promotion.PromotionPaymentMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getEvent;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getEventWithSecondId;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getUserPromotion;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPaymentDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPaymentDtoWithStatus;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPaymentDtoWhenDeclined;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPlanDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPlanEventDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionRequestDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionRequestDtoWithEvent;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionResponseDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionResponseDtoWhenDeclined;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionResponseWithEventDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionWithEvent;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionWithEventWhenInactive;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getUserWithEvent;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PromotionPlanService promotionPlanService;

    @Mock
    private PromotionPaymentService promotionPaymentService;

    @Spy
    private PromotionMapperImpl promotionMapper;

    @Spy
    private PromotionPaymentMapper promotionPaymentMapper;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    @Test
    public void testGetPromotionsByUser() {
        when(promotionRepository.findPromotionByUserId(eq(1L)))
                .thenReturn(List.of(getUserPromotion()));

        List<PromotionResponseDto> promotionsByUserDto = promotionService.getPromotionsByUser(1L);

        assertEquals(List.of(getPromotionResponseDto()), promotionsByUserDto);
    }

    @Test
    public void testCreatePromotionWhenUserNotExist() {
        when(promotionPaymentService.sendAndCreate(eq(getPromotionRequestDto()))).thenReturn(getPromotionPaymentDto());
        when(promotionPlanService.getPromotionPlanByName(eq("BASIC"))).thenReturn(getPromotionPlanDto());
        when(userRepository.findById(eq((1L)))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> promotionService.createPromotion(getPromotionRequestDto()));
    }

    @Test
    public void testCreatePromotionWithEventTypeWhenEventNotExist() {
        when(promotionPaymentService.sendAndCreate(eq(getPromotionRequestDtoWithEvent()))).thenReturn(getPromotionPaymentDto());
        when(promotionPlanService.getPromotionPlanByName(eq("BASIC"))).thenReturn(getPromotionPlanDto());
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUserWithEvent()));
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEventWithSecondId()));

        assertThrows(EntityNotFoundException.class, () -> promotionService.createPromotion(getPromotionRequestDtoWithEvent()));
    }

    @Test
    public void testCreatePromotionWithEventTypeWhenEventExist() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUserWithEvent()));
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        when(promotionPaymentService.sendAndCreate(getPromotionRequestDtoWithEvent())).thenReturn(getPromotionPaymentDtoWithStatus());
        when(promotionPlanService.getPromotionPlanByName("BASIC")).thenReturn(getPromotionPlanEventDto());
        when(promotionRepository.save(any())).thenReturn(getPromotionWithEvent());

        PromotionResponseDto promotion = promotionService.createPromotion(getPromotionRequestDtoWithEvent());

        assertEquals(getPromotionResponseWithEventDto(), promotion);
    }

    @Test
    public void testCreatePromotionWhenPaymentDecline() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUserWithEvent()));
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        when(promotionPaymentService.sendAndCreate(getPromotionRequestDtoWithEvent()))
                .thenReturn(getPromotionPaymentDtoWhenDeclined());
        when(promotionPlanService.getPromotionPlanByName("BASIC")).thenReturn(getPromotionPlanEventDto());
        when(promotionRepository.save(any())).thenReturn(getPromotionWithEventWhenInactive());

        PromotionResponseDto promotion = promotionService.createPromotion(getPromotionRequestDtoWithEvent());

        assertEquals(getPromotionResponseDtoWhenDeclined(), promotion);
    }

}