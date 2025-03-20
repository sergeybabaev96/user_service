package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SkillUserGuaranteeTest {

    @Mock
    private UserSkillGuaranteeRepository guaranteeRepository;
    @Mock
    private SkillOfferService skillOfferService;

    @InjectMocks
    private SkillUserGuarantee skillUserGuarantee;

    @Test
    public void testAddUserSkillGuarantee() {

        long skillId = 1;
        long userId = 1;
        SkillOffer offer1 = new SkillOffer();
        SkillOffer offer2 = new SkillOffer();
        Skill skill = new Skill();
        offer1.setId(skillId);
        offer2.setId(userId);
        offer1.setSkill(skill);
        offer2.setSkill(skill);
        Recommendation recommendation = new Recommendation();
        User user = new User();
        recommendation.setAuthor(user);
        offer1.setRecommendation(recommendation);
        offer2.setRecommendation(recommendation);
        List<SkillOffer> skillOffers = List.of(offer1, offer2);
        when(skillOfferService.getSkillOfferToUser(skillId, userId)).thenReturn(skillOffers);
        skillUserGuarantee.addUserSkillGuarantee(skillId, userId);
        verify(skillOfferService,times(1)).getSkillOfferToUser(skillId, userId);
        verify(guaranteeRepository, times(2)).save(any());
    }
}