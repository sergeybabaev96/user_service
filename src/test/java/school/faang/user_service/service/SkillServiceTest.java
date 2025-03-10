package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    private static final long USER_ID = 12345L;
    private static final long SKILL_ID1 = 123456L;
    private static final long SKILL_ID2 = 123457L;
    private static final long SKILL_ID3 = 123458L;

    private static final String SKILL_TITLE1 = "-title-1-";
    private static final String SKILL_TITLE2 = "-title-2-";
    private static final String SKILL_TITLE3 = "-title-3-";

    private static Skill sentSkill1;
    private static Skill sentSkill2;
    private static Skill sentSkill3;

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;

    @InjectMocks
    private SkillService skillService;

    @BeforeAll
    static void init() {
        sentSkill1 = new Skill();
        sentSkill1.setId(SKILL_ID1);
        sentSkill1.setTitle(SKILL_TITLE1);

        sentSkill2 = new Skill();
        sentSkill2.setId(SKILL_ID2);
        sentSkill2.setTitle(SKILL_TITLE2);

        sentSkill3 = new Skill();
        sentSkill3.setId(SKILL_ID3);
        sentSkill3.setTitle(SKILL_TITLE3);
    }

    @Test
    void createWhenValidationIsIncorrect() {
        doReturn(true).when(skillRepository).existsByTitle(any(String.class));

        assertThrows(DataValidationException.class, () -> skillService.create(sentSkill3));
    }

    @Test
    void createCompleteSuccess() {
        skillService.create(sentSkill3);
        verify(skillRepository, times(1)).save(sentSkill3);
    }

    @Test
    void getUserSkills() {
        skillService.getUserSkills(USER_ID);

        verify(skillRepository, times(1)).findAllByUserId(USER_ID);
    }

    @Test
    void getOfferedSkills() {
        List<Skill> sentOfferedSkills = List.of(sentSkill1, sentSkill2, sentSkill1, sentSkill1, sentSkill1);
        final long amountOfSkill1 = 4;
        final long amountOfSkill2 = 1;

        doReturn(sentOfferedSkills).when(skillRepository).findSkillsOfferedToUser(USER_ID);
        Map<Skill, Long> returnedOfferedSkills = skillService.getOfferedSkills(USER_ID);

        assertEquals(2, returnedOfferedSkills.size());
        assertEquals(amountOfSkill1, returnedOfferedSkills.get(sentSkill1));
        assertEquals(amountOfSkill2, returnedOfferedSkills.get(sentSkill2));
    }

    @Test
    void acquireSkillFromOffersWhenNotEnoughConfirmations() {
        List<SkillOffer> returnedSkillOffers = List.of(new SkillOffer(), new SkillOffer());

        doReturn(Optional.empty())
                .when(skillRepository).findUserSkill(SKILL_ID1, USER_ID);
        doReturn(returnedSkillOffers).when(skillOfferRepository).findAllOffersOfSkill(SKILL_ID1, USER_ID);

        Skill returnedSkill = skillService.acquireSkillFromOffers(SKILL_ID1, USER_ID);

        verify(skillRepository, never()).assignSkillToUser(SKILL_ID1, USER_ID);
    }

    @Test
    void acquireSkillFromOffersWhenUserAlreadyHaveThisSkill() {
        doReturn(Optional.of(sentSkill1))
                .when(skillRepository).findUserSkill(SKILL_ID1, USER_ID);

        Skill returnedSkill = skillService.acquireSkillFromOffers(SKILL_ID1, USER_ID);

        assertEquals(SKILL_ID1, returnedSkill.getId());
        assertEquals(SKILL_TITLE1, returnedSkill.getTitle());
        verify(skillRepository, never()).assignSkillToUser(SKILL_ID1, USER_ID);    }

    @Test
    void acquireSkillFromOffersCompleteSuccess() {
        List<SkillOffer> returnedSkillOffers = List.of(new SkillOffer(), new SkillOffer(), new SkillOffer());

        doReturn(Optional.empty())
                .doReturn(Optional.of(sentSkill1))
                .when(skillRepository).findUserSkill(SKILL_ID1, USER_ID);
        doReturn(returnedSkillOffers).when(skillOfferRepository).findAllOffersOfSkill(SKILL_ID1, USER_ID);

        Skill returnedSkill = skillService.acquireSkillFromOffers(SKILL_ID1, USER_ID);

        assertEquals(SKILL_ID1, returnedSkill.getId());
        assertEquals(SKILL_TITLE1, returnedSkill.getTitle());
        verify(skillRepository, atLeastOnce()).assignSkillToUser(SKILL_ID1, USER_ID);
    }
}