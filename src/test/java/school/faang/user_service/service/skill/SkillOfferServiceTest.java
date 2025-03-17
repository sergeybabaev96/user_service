package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillOfferServiceTest {

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @InjectMocks
    private SkillOfferService skillOfferService;

    @Test
    public void testIsEnoughAmountOffersToSkillWithWrong() {
        long skillOfferId = 1L;
        long userId = 1L;
        SkillOffer skillOffered1 = new SkillOffer();
        SkillOffer skillOffered2 = new SkillOffer();
        skillOffered1.setId(skillOfferId);
        skillOffered2.setId(skillOfferId);
        List<SkillOffer> skillOffers = List.of(skillOffered1, skillOffered2);
        when(skillOfferRepository.findAllOffersOfSkill(skillOfferId, userId)).thenReturn(skillOffers);
        assertThrows(DataValidationException.class, () -> skillOfferService.isEnoughAmountOffersToSkill(skillOfferId, userId));
    }

    @Test
    public void testIsEnoughAmountOffersToSkill() {
        long skillOfferId = 1L;
        long userId = 1L;
        SkillOffer skillOffered1 = new SkillOffer();
        SkillOffer skillOffered2 = new SkillOffer();
        SkillOffer skillOffered3 = new SkillOffer();
        skillOffered1.setId(skillOfferId);
        skillOffered2.setId(skillOfferId);
        skillOffered3.setId(skillOfferId);
        List<SkillOffer> skillOffers = List.of(skillOffered1, skillOffered2, skillOffered3);
        when(skillOfferRepository.findAllOffersOfSkill(skillOfferId, userId)).thenReturn(skillOffers);
        skillOfferService.isEnoughAmountOffersToSkill(skillOfferId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillOfferId, userId);


    }

}