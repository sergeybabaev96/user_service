package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.*;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    private static final long SKILL_ID = 1L;
    private static final long USER_ID = 1L;
    private static final int OFFERS_AMOUNT = 1;
    private static final int MIN_SKILL_OFFERS = 3;

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository guaranteeRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private SkillMapperImpl skillMapper;
    @Mock
    private SkillCandidateMapperImpl skillCandidateMapper;
    private Skill skill;
    private SkillDto skillDto;
    private List<Skill> skills;
    private List<SkillDto> skillsDto;
    private User user;


    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(USER_ID)
                .build();

        skill = Skill.builder()
                .id(SKILL_ID)
                .title("testTitle")
                .users(Collections.singletonList(user))
                .guarantees(new ArrayList<>())
                .build();

        skillDto = SkillDto.builder()
                .id(skill.getId())
                .title(skill.getTitle())
                .build();

        skills = IntStream.range(0, 5)
                .mapToObj(i -> Skill.builder()
                        .id(i)
                        .build())
                .toList();

        skillsDto = skills.stream()
                .map(skill1 -> SkillDto.builder()
                        .id(skill1.getId())
                        .build())
                .toList();
    }

    @Test
    public void shouldSuccessCreate() {
        doNothing().when(skillValidator).validateSkill(any(SkillDto.class));

        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(userService.getAllByIds(skillDto.userIds())).thenReturn(Collections.emptyList());
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto createdSkillDto = skillService.create(skillDto);

        verify(skillValidator).validateSkill(skillDto);
        verify(skillMapper).toEntity(skillDto);
        verify(userService).getAllByIds(skillDto.userIds());
        verify(skillRepository).save(skill);
        verify(skillMapper).toDto(skill);

        Assertions.assertEquals(skillDto, createdSkillDto);
    }

    @Test
    public void shouldSuccessGetUsersSkills() {
        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(skills);
        skills.forEach(skill1 -> when(skillMapper.toDto(skill1))
                .thenReturn(SkillDto.builder()
                        .id(skill1.getId())
                        .build()));

        List<SkillDto> result = skillService.getUsersSkills(USER_ID);

        Assertions.assertEquals(skills.size(), result.size());
        IntStream.range(0, result.size())
                .forEach(i -> Assertions.assertEquals(result.get(i), skillsDto.get(i)));

        verify(skillRepository).findAllByUserId(USER_ID);
    }

    @Test
    public void shouldSuccessGetOfferedSkills() {
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);

        List<SkillCandidateDto> candidatesDto = IntStream.range(0, skills.size())
                .mapToObj(i -> SkillCandidateDto.builder()
                        .skillDto(skillsDto.get(i))
                        .offersAmount(OFFERS_AMOUNT)
                        .build())
                .toList();

        IntStream.range(0, skills.size()).forEach(i -> {
            Skill skill = skills.get(i);
            when(skillOfferRepository
                    .countAllOffersOfSkill(skill.getId(), USER_ID)).thenReturn(OFFERS_AMOUNT);
            when(skillCandidateMapper
                    .toDto(skill, OFFERS_AMOUNT)).thenReturn(candidatesDto.get(i));
        });

        List<SkillCandidateDto> result = skillService.getOfferedSkills(USER_ID);

        Assertions.assertEquals(skills.size(), result.size());
        IntStream.range(0, skills.size())
                .forEach(i -> Assertions.assertEquals(candidatesDto.get(i), result.get(i)));

        verify(skillRepository).findSkillsOfferedToUser(USER_ID);
        IntStream.range(0, skills.size()).forEach(i -> {
            Skill skill = skills.get(i);
            verify(skillOfferRepository).countAllOffersOfSkill(skill.getId(), USER_ID);
            verify(skillCandidateMapper).toDto(skill, OFFERS_AMOUNT);
        });
    }

    @Test
    public void shouldUserHasSkillInvalid(){
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(skill));

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
        Assertions.assertEquals(MessageError.USER_ALREADY_HAS_SUGGESTED_SKILL.name(), exception.getMessage());

        verify(skillRepository).findUserSkill(SKILL_ID, USER_ID);
        verifyNoInteractions(skillOfferRepository, skillMapper);
    }

    @Test
    public void shouldNotEnoughSkillOffersInvalid(){
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(userService.getById(USER_ID)).thenReturn(User.builder().build());
        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID))
                .thenReturn(Collections.emptyList());

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
        Assertions.assertEquals(MessageError.NOT_ENOUGH_SKILL_OFFERS.name(), exception.getMessage());

        verify(skillOfferRepository).findAllOffersOfSkill(SKILL_ID, USER_ID);
        verify(userService).getById(USER_ID);
        verify(skillRepository).findUserSkill(SKILL_ID, USER_ID);
        verifyNoInteractions(skillMapper);
    }

    @Test
    public void shouldNotSkillAvailableInvalid(){
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());
        when(userService.getById(USER_ID)).thenReturn(User.builder().build());

        List<SkillOffer> offers = Collections.nCopies(MIN_SKILL_OFFERS, new SkillOffer());

        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(offers);
        doNothing().when(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
        Assertions.assertEquals(MessageError.SKILL_NOT_AVAILABLE.name(), exception.getMessage());

        verify(skillOfferRepository).findAllOffersOfSkill(SKILL_ID, USER_ID);
        verify(userService).getById(USER_ID);
        verify(skillRepository, times(2)).findUserSkill(SKILL_ID, USER_ID);
        verifyNoInteractions(skillMapper);
    }

    @Test
    public void shouldSuccessAcquireSkillFromOffers(){
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(skill));
        when(userService.getById(USER_ID)).thenReturn(user);

        List<SkillOffer> offers = Collections.nCopies(MIN_SKILL_OFFERS, SkillOffer.builder()
                .skill(skill)
                .build());

        when(skillOfferRepository.findAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(offers);
        doNothing().when(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);

        when(skillMapper.toDto(skill)).thenReturn(skillDto);
        when(guaranteeRepository.save(any(UserSkillGuarantee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SkillDto result = skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        Assertions.assertEquals(skillDto, result);

        verify(skillRepository, times(2)).findUserSkill(SKILL_ID, USER_ID);
        verify(userService, times(1)).getById(USER_ID);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(SKILL_ID, USER_ID);
        verify(skillRepository, times(1)).assignSkillToUser(SKILL_ID, USER_ID);
        verify(skillRepository, times(2)).findUserSkill(SKILL_ID, USER_ID);
        verify(skillMapper, times(1)).toDto(skill);
        verify(guaranteeRepository,times(offers.size())).save(any(UserSkillGuarantee.class));
    }
}