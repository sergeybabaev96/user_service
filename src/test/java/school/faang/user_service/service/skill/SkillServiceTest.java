package school.faang.user_service.service.skill;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.user.UserSkillGuaranteeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    SkillOfferService skillOfferService;

    @Mock
    UserSkillGuaranteeService userSkillGuaranteeService;

    @Spy
    private SkillMapperImpl skillMapper;

    @Spy
    private SkillCandidateMapper skillCandidateMapper;


    private final String title = "Java";
    private final SkillDto skillDto = SkillDto.builder().title(title).build();
    private final Skill skill = Skill.builder().title(title).build();
    private final Skill skillFromBd = Skill.builder().id(1L).title(title).build();
    private final long userId = 1L;
    private final long skillId = 1L;
    private final List<Skill> skills = List.of(
            Skill.builder().id(1L).title("Java").build(),
            Skill.builder().id(2L).title("Python").build(),
            Skill.builder().id(3L).title("C++").build(),
            Skill.builder().id(4L).title("C#").build()
    );


    @Test
    void testCreate() {
        SkillDto expectedDto = skillMapper.toDto(skillFromBd);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skillFromBd);

        SkillDto createdSkillDto = skillService.create(skillDto);

        assertEquals(expectedDto, createdSkillDto);
        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillMapper, times(1)).toEntity(skillDto);
        verify(skillRepository, times(1)).save(skill);
        verify(skillMapper, times(2)).toDto(skillFromBd);
    }

    @Test
    void testNotCreatedWhenTitleExists() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
    }

    @Test
    void testGetUserSkills() {
        long userId = 1;

        List<SkillDto> expectedDtos = skills.stream().map(skillMapper::toDto).toList();

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<SkillDto> resultDtos = skillService.getUserSkills(userId);

        assertEquals(expectedDtos, resultDtos);
        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, times(skills.size() * 2)).toDto(any());
    }

    @Test
    void testGetOfferedSkills() {
        List<SkillCandidateDto> expectedDtos = skillCandidateMapper.toSkillCandidateDtoList(skills);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);
        List<SkillCandidateDto> resultDtos = skillService.getOfferedSkills(userId);

        assertEquals(expectedDtos, resultDtos);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    void testAcquireSkillFromOffers() {

        Skill skill1 = Skill.builder().id(skillId).title("Java").build();
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill1));
        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(skillMapper.toDto(skill1), result);

        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferService, never()).findAllOffersOfSkill(userId, skillId);
        verify(skillMapper, times(2)).toDto(skill1);
    }

    @Test
    void testSkillFromOffersWhenSkillExists() {
        List<SkillOffer> skillOffers = generateSkillOffers(3);
        Skill assignedSkill = Skill.builder().id(skillId).title("Java").build();

        when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(assignedSkill));

        when(skillOfferService.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        doNothing().when(skillRepository).assignSkillToUser(skillId, userId);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(skillMapper.toDto(assignedSkill), result);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(skillRepository, times(3)).findUserSkill(skillId, userId);
        verify(userSkillGuaranteeService, times(skillOffers.size())).save(any());
        verify(skillMapper, times(2)).toDto(assignedSkill);
    }

    @Test
    void testSkillNotAssignedWhenNotEnoughOffers() {
        List<SkillOffer> skillOffers = generateSkillOffers(1);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferService.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));

        verify(skillRepository, never()).assignSkillToUser(skillId, userId);
    }

    @Test
    void testSkillAssignmentFails() {
        List<SkillOffer> skillOffers = generateSkillOffers(3);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferService.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        doNothing().when(skillRepository).assignSkillToUser(skillId, userId);
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
        assertEquals("Error adding a skill to a user", exception.getMessage());
    }


    private List<SkillOffer> generateSkillOffers(int count) {
        return IntStream.range(0, count).mapToObj(num -> SkillOffer.builder()
                        .skill(Skill.builder().id(skillId).title("Java").build())
                        .recommendation(Recommendation.builder()
                                .author(User.builder().build())
                                .receiver(User.builder().build())
                                .build())
                        .build())
                .toList();
    }
}
