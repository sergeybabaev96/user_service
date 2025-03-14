package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.implementation.SkillServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillServiceImpl skillService;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> guaranteeCaptor;
    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;
    @Captor
    private ArgumentCaptor<List<Skill>> skillsCaptor;

    @Test
    void testCreate_withParameterMethod_isNull() {
        assertThrows(DataValidationException.class, () -> skillService.create(null));
    }

    @Test
    void testCreate_withTitle_isNull() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreate_withTitle_isBlank() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("   ");
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreate_withTitleExists() {
        SkillDto skillDto = new SkillDto();
        prepareDataTestCreate(true, skillDto);
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreate_savesSkill() {
        SkillDto skillDto = new SkillDto();
        prepareDataTestCreate(false, skillDto);
        Skill mappedSkill = skillMapper.toEntity(skillDto);
        ArgumentCaptor<Skill> captor = ArgumentCaptor.forClass(Skill.class);
        when(skillRepository.save(mappedSkill)).thenReturn(mappedSkill);

        SkillDto result = skillService.create(skillDto);

        verify(skillRepository, times(1)).save(captor.capture());
        Skill capturedSkill = captor.getValue();
        assertEquals(skillDto.getTitle(), capturedSkill.getTitle());

        assertEquals(skillDto.getTitle(), result.getTitle());
    }

    @Test
    void testGetUserSkills() {
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);
        Skill firstSkill = new Skill();
        firstSkill.setTitle("Title1");
        firstSkill.setUsers(List.of(firstUser, secondUser));
        Skill secondSkill = new Skill();
        secondSkill.setTitle("Title2");
        secondSkill.setUsers(List.of(firstUser, secondUser));
        List<Skill> expectedSkills = List.of(firstSkill, secondSkill);
        List<SkillDto> expectedSkillsDto = expectedSkills
                .stream()
                .map(skillMapper::toDto)
                .toList();
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillRepository.findAllByUserId(firstUser.getId())).thenReturn(List.of(firstSkill, secondSkill));

        List<SkillDto> actualSkillsDto = skillService.getUserSkills(firstUser.getId());

        verify(skillRepository, times(1)).findAllByUserId(userIdCaptor.capture());
        Long firstUserId = userIdCaptor.getValue();
        assertEquals(firstUser.getId(), firstUserId);
        assertEquals(firstSkill.getTitle(), actualSkillsDto.get(0).getTitle());
        assertEquals(secondSkill.getTitle(), actualSkillsDto.get(1).getTitle());
        assertIterableEquals(expectedSkillsDto, actualSkillsDto);
    }

    @Test
    void testGetOfferedSkills() {
        User user = new User();
        user.setId(1L);
        Skill firstSkill = new Skill();
        firstSkill.setUsers(List.of(user));
        Skill secondSkill = new Skill();
        secondSkill.setUsers(List.of(user));
        List<Skill> skills = List.of(firstSkill, secondSkill);
        Map<Skill, Long> skillCountMap = skills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        List<SkillCandidateDto> expectedSkillCandidatesDto = skillCountMap
                .entrySet()
                .stream()
                .map(entry -> skillMapper.toSkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillRepository.findSkillsOfferedToUser(user.getId())).thenReturn(List.of(firstSkill, secondSkill));

        List<SkillCandidateDto> actualSkillCandidatesDto = skillService.getOfferedSkills(user.getId());

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userIdCaptor.capture());
        Long firstUserId = userIdCaptor.getValue();
        assertEquals(user.getId(), firstUserId);
        assertIterableEquals(expectedSkillCandidatesDto, actualSkillCandidatesDto);
    }

    @Test
    void acquireSkillFromOffers_notFindsBySkillId() {
        Skill skill = new Skill();
        skill.setId(1L);
        User user = new User();
        user.setId(1L);
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skill.getId(), user.getId()));
    }

    @Test
    void acquireSkillFromOffers_findsByUserSkill() {
        Skill skill = new Skill();
        skill.setId(1L);
        User user = new User();
        user.setId(1L);
        Optional<Skill> acquiredSkill = Optional.of(skill);
        SkillDto expectedSkillDto = skillMapper.toDto(acquiredSkill.get());
        ArgumentCaptor<Long> skillIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillRepository.findById(skill.getId()))
                .thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skill.getId(), user.getId()))
                .thenReturn(acquiredSkill);

        SkillDto actualSkillDto = skillService.acquireSkillFromOffers(skill.getId(), user.getId());

        verify(skillRepository, times(1))
                .findById(skill.getId());
        verify(skillRepository, times(1))
                .findUserSkill(skillIdCaptor.capture(), userIdCaptor.capture());
        Long skillId = skillIdCaptor.getValue();
        Long userId = userIdCaptor.getValue();
        assertEquals(skill.getId(), skillId);
        assertEquals(user.getId(), userId);
        assertEquals(expectedSkillDto, actualSkillDto);
    }

    @Test
    void acquireSkillFromOffers_notFinds_whenOffersSize_lessThan_minSkillOffers() {
        Skill skill = new Skill();
        skill.setId(1L);
        User user = new User();
        user.setId(1L);
        SkillOffer skillOffer = new SkillOffer();
        List<SkillOffer> offers = List.of(skillOffer);
        when(skillRepository.findById(skill.getId()))
                .thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skill.getId(), user.getId()))
                .thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skill.getId(), user.getId()))
                .thenReturn(offers);

        assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skill.getId(), user.getId()));
    }

    @Test
    void acquireSkillFromOffers_savesAllGuarantees() {
        Skill skill = new Skill();
        skill.setId(1L);
        User user = new User();
        user.setId(1L);
        SkillOffer firstSkillOffer = new SkillOffer();
        firstSkillOffer.setSkill(skill);
        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(user);
        recommendation.setReceiver(user);
        firstSkillOffer.setRecommendation(recommendation);
        SkillOffer secondSkillOffer = new SkillOffer();
        secondSkillOffer.setSkill(skill);
        secondSkillOffer.setRecommendation(recommendation);
        SkillOffer thirdSkillOffer = new SkillOffer();
        thirdSkillOffer.setSkill(skill);
        thirdSkillOffer.setRecommendation(recommendation);
        List<SkillOffer> offers = List.of(firstSkillOffer, secondSkillOffer, thirdSkillOffer);
        List<UserSkillGuarantee> guarantees = offers.stream()
                .filter(offer -> Objects.nonNull(offer.getRecommendation().getReceiver())
                        && Objects.nonNull(offer.getRecommendation().getAuthor()))
                .map(offer -> UserSkillGuarantee.builder()
                        .user(offer.getRecommendation().getReceiver())
                        .skill(offer.getSkill())
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build())
                .collect(Collectors.toList());
        ArgumentCaptor<Long> skillIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        SkillDto expectedSkillDto = skillMapper.toDto(skill);

        when(skillRepository.findById(skill.getId()))
                .thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skill.getId(), user.getId()))
                .thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skill.getId(), user.getId()))
                .thenReturn(offers);

        SkillDto actualSkillDto = skillService.acquireSkillFromOffers(skill.getId(), user.getId());

        verify(skillRepository, times(1))
                .findById(skill.getId());
        verify(skillRepository, times(1))
                .findUserSkill(skill.getId(), user.getId());
        verify(skillOfferRepository, times(1))
                .findAllOffersOfSkill(skill.getId(), user.getId());
        verify(skillRepository, times(1))
                .assignSkillToUser(skillIdCaptor.capture(), userIdCaptor.capture());
        Long captorSkillId = skillIdCaptor.getValue();
        Long captorUserId = userIdCaptor.getValue();
        assertEquals(skill.getId(), captorSkillId);
        assertEquals(user.getId(), captorUserId);
        verify(userSkillGuaranteeRepository, times(1))
                .saveAll(guaranteeCaptor.capture());
        List<UserSkillGuarantee> captorGuarantees = guaranteeCaptor.getValue();
        assertIterableEquals(guarantees, captorGuarantees);
        assertEquals(expectedSkillDto, actualSkillDto);
    }

    @Test
    void findAllSkillsById() {
        Skill firstSkill = new Skill();
        firstSkill.setId(1L);
        Skill secondSkill = new Skill();
        secondSkill.setId(2L);
        List<Long> skillIds = List.of(firstSkill.getId(), secondSkill.getId());
        List<Skill> skills = List.of(firstSkill, secondSkill);
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        List<Skill> actualAllSkillsById = skillService.findAllSkillsById(skillIds);

        verify(skillRepository, times(1))
                .findAllById(idsCaptor.capture());
        List<Long> idsCaptorValue = idsCaptor.getValue();
        assertIterableEquals(skillIds, idsCaptorValue);
        assertIterableEquals(skills, actualAllSkillsById);
    }

    @Test
    void findSkillsByUserId() {
        User user = new User();
        user.setId(1L);
        List<User> users = List.of(user);
        Skill firstSkill = new Skill();
        firstSkill.setUsers(users);
        Skill secondSkill = new Skill();
        secondSkill.setUsers(users);
        List<Skill> skills = List.of(firstSkill, secondSkill);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillRepository.findAllByUserId(user.getId())).thenReturn(skills);

        List<Skill> actualSkillsByUserId = skillService.findSkillsByUserId(user.getId());

        verify(skillRepository, times(1)).findAllByUserId(userIdCaptor.capture());
        Long userIdCaptorValue = userIdCaptor.getValue();
        assertEquals(user.getId(), userIdCaptorValue);
        assertIterableEquals(skills, actualSkillsByUserId);
    }

    @Test
    void findSkillsByGoalId() {
        Goal goal = new Goal();
        goal.setId(1L);
        List<Goal> goals = List.of(goal);
        Skill firstSkill = new Skill();
        firstSkill.setGoals(goals);
        Skill secondSkill = new Skill();
        secondSkill.setGoals(goals);
        List<Skill> skills = List.of(firstSkill, secondSkill);
        ArgumentCaptor<Long> goalIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(skillRepository.findSkillsByGoalId(goal.getId())).thenReturn(skills);

        List<Skill> actualSkillsByGoalId = skillService.findSkillsByGoalId(goal.getId());

        verify(skillRepository, times(1)).findSkillsByGoalId(goalIdCaptor.capture());
        Long goalIdCaptorValue = goalIdCaptor.getValue();
        assertEquals(goal.getId(), goalIdCaptorValue);
        assertIterableEquals(skills, actualSkillsByGoalId);
    }

    @Test
    void saveAllSkills() {
        Goal goal = new Goal();
        goal.setId(1L);
        List<Goal> goals = List.of(goal);
        Skill firstSkill = new Skill();
        firstSkill.setGoals(goals);
        Skill secondSkill = new Skill();
        secondSkill.setGoals(goals);
        List<Skill> skills = List.of(firstSkill, secondSkill);

        skillService.saveAllSkills(skills);

        verify(skillRepository, times(1)).saveAll(skillsCaptor.capture());
        List<Skill> skillsCaptorValue = skillsCaptor.getValue();
        assertIterableEquals(skills, skillsCaptorValue);
    }

    private void prepareDataTestCreate(boolean existsByTitle, SkillDto skillDto) {
        skillDto.setTitle("Title");
        String title = "Title";
        assertEquals("Title", skillDto.getTitle());
        assertEquals("Title", title);
        assertEquals(skillDto.getTitle(), title);
        when(skillRepository.existsByTitle("Title")).thenReturn(existsByTitle);
    }
}
