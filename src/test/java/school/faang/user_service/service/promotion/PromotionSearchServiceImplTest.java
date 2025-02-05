package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getEventPromotion;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPlanPremium;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getUserDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getUserPromotion;

@ExtendWith(MockitoExtension.class)
class PromotionSearchServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionPlanRepository promotionPlanRepository;

    @Mock
    private PromotionService promotionService;

    @Spy
    private UserMapperImpl userMapper;

    @Spy
    private EventMapperImpl eventMapper;

    @InjectMocks
    private PromotionSearchServiceImpl promotionSearchService;

    @Test
    public void testSearchResults() {
        when(promotionRepository.findPromotedUsersByQuery(eq("user and event"))).thenReturn(List.of(getUserPromotion()));
        when(promotionRepository.findPromotedEventsByQuery(eq("user and event"))).thenReturn(List.of(getEventPromotion()));
        when(promotionPlanRepository.findPromotionPlanByName(eq("PREMIUM")))
                .thenReturn(Optional.ofNullable(getPromotionPlanPremium()));
        doNothing().when(promotionService).updatePromotionViews(eq(1L));

        List<Object> result = promotionSearchService.searchResults("user and event", 5);

        assertEquals(result.get(0), getUserDto());
    }
}