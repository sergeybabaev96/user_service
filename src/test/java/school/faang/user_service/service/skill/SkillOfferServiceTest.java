package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillOfferServiceTest {

    @InjectMocks
    private SkillOfferService skillOfferService;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Test
    void testFindAllOffersOfSkill() {
        List<SkillOffer> expectedOffers = List.of(
                new SkillOffer(),
                new SkillOffer()
        );

        long skillId = 1L;
        long userId = 1L;

        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(expectedOffers);
        List<SkillOffer> result = skillOfferService.findAllOffersOfSkill(skillId, userId);

        assertEquals(expectedOffers, result);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
    }
}
