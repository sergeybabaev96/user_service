package school.faang.user_service.service.promotion;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.enums.promotion.PromotionTariff;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.mapper.promotion.PromotionPaymentMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionPlanRepository promotionPlanRepository;
    private final PromotionPlanService promotionPlanService;
    private final PromotionPaymentService promotionPaymentService;
    private final PromotionMapper promotionMapper;
    private final PromotionPaymentMapper promotionPaymentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<PromotionResponseDto> getPromotionsByUser(long userId) {
        log.info("Get promotions by user with id: {}", userId);
        return promotionRepository.findPromotionByUserId(userId).stream()
                .map(promotionMapper::toDto)
                .toList();
    }

    @Override
    public List<PromotionResponseDto> getPromotionsByEvent(long eventId) {
        log.info("Get promotions by event with id: {}", eventId);
        return promotionRepository.findPromotionByEventId(eventId).stream()
                .map(promotionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PromotionResponseDto createPromotion(PromotionRequestDto dto) {
        log.info("Create promotion: {}", dto);
        PromotionPaymentDto newPayment = promotionPaymentService.sendAndCreate(dto);

        Promotion promotion = createPromotion(dto, newPayment);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return promotionMapper.toDto(savedPromotion);
    }

    @Override
    @Transactional
    public void updatePromotionViews(Long promotionId) {
        log.info("Update views promotion with id = {}", promotionId);
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with id: " + promotionId));
        if (promotion.getUsedViews() < getPromotionPlanByTariff(promotion.getTariff()).getViewsCount()) {
            promotion.setUsedViews(promotion.getUsedViews() + 1);
        }
        if (promotion.getUsedViews() >= getPromotionPlanByTariff(promotion.getTariff()).getViewsCount()) {
            promotion.setStatus(PromotionStatus.INACTIVE);
        }
        promotionRepository.save(promotion);
    }

    private Promotion createPromotion(PromotionRequestDto dto, PromotionPaymentDto payment) {
        PromotionPlanDto promotionPlan = promotionPlanService.getPromotionPlanByName(dto.getTariff().getValue());
        User user = getUser(dto);

        return createPromotion(dto, payment, user, promotionPlan);
    }

    private Promotion createPromotion(PromotionRequestDto dto, PromotionPaymentDto payment, User user,
                                      PromotionPlanDto promotionPlan) {
        return Promotion.builder()
                .user(user)
                .event(getEvent(dto, user))
                .tariff(dto.getTariff())
                .planType(dto.getPlanType())
                .usedViews(promotionPlan.getViewsCount())
                .status(getPromotionStatus(payment.getStatus()))
                .promotionPayment(promotionPaymentMapper.toEntity(payment))
                .build();
    }

    private User getUser(PromotionRequestDto dto) {
        return userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with id = %d doesn't exists", dto.getUserId())));
    }

    private Event getEvent(PromotionRequestDto dto, User user) {
        Event event = null;
        if (dto.getEventId() != null) {
            event = eventRepository.findById(dto.getEventId()).orElse(null);
            checkEvent(dto, user);
        }
        return event;
    }

    private void checkEvent(PromotionRequestDto dto, User user) {
        if (PromotionPlanType.EVENT.equals(dto.getPlanType())) {
            Long eventId = dto.getEventId();
            Event event = eventRepository.findById(eventId).orElseThrow(() ->
                    new EntityNotFoundException("Event not found"));
            checkUserHaveEvent(user, event);
        }
    }

    private static void checkUserHaveEvent(User user, Event event) {
        if (!user.getOwnedEvents().contains(event)) {
            throw new EntityNotFoundException(String.format("User haven't event with id = %d", event.getId()));
        }
    }

    private PromotionStatus getPromotionStatus(PromotionPaymentStatus status) {
        if (PromotionPaymentStatus.ACCEPTED.equals(status)) {
            return PromotionStatus.ACTIVE;
        } else {
            return PromotionStatus.INACTIVE;
        }
    }

    private PromotionPlan getPromotionPlanByTariff(PromotionTariff tariff) {
        return promotionPlanRepository.findPromotionPlanByName(tariff.getValue()).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Promotion plan with tariff = %s not found", tariff.getValue())));
    }
}
