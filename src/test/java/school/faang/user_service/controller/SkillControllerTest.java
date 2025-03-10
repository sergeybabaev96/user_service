package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;
import school.faang.user_service.controller.skill.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.exception.DataValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;
    @Spy
    private SkillMapperImpl skillMapper;

    @InjectMocks
    private SkillController skillController;

    private static final String SKILL_TITLE1 = "-title-1-";
    private static final String SKILL_TITLE2 = "-title-2-";
    private static final String SKILL_TITLE3 = "-title-3-";
    private static final Long SKILL_ID1 = 12345L;
    private static final Long SKILL_ID2 = 23456L;
    private static final Long SKILL_ID3 = 34567L;

    private Skill skill;
    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setTitle(SKILL_TITLE1);
        skill.setId(SKILL_ID1);

        skillDto = new SkillDto(SKILL_ID1, SKILL_TITLE1);
    }

    @Test
    void create() {
        SkillDto sentDto = new SkillDto(SKILL_ID1, SKILL_TITLE1);
        Skill sentEntity = skillMapper.toEntity(sentDto);

        when(skillService.create(sentEntity)).thenReturn(sentEntity);
        SkillDto returnedDto = skillController.create(sentDto);

        assertEquals(sentDto.getTitle(), returnedDto.getTitle());
    }

    @Test
    void getUserSkills() {
        final long userId = 56789L;
        List<Skill> sentSkills = new ArrayList<>();
        sentSkills.add(skillMapper.toEntity(new SkillDto(SKILL_ID1, SKILL_TITLE1)));
        sentSkills.add(skillMapper.toEntity(new SkillDto(SKILL_ID2, SKILL_TITLE2)));
        sentSkills.add(skillMapper.toEntity(new SkillDto(SKILL_ID3, SKILL_TITLE3)));

        when(skillService.getUserSkills(userId)).thenReturn(sentSkills);
        List<SkillDto> returnedDtos = skillController.getUserSkills(userId);

        assertEquals(sentSkills.size(), returnedDtos.size());
        for (Skill skill : sentSkills) {
            Optional<SkillDto> returnedDtoOtp = returnedDtos.stream()
                    .filter(s -> skill.getId() == s.getId() && skill.getTitle().equals(s.getTitle()))
                    .findFirst();
            assertTrue(returnedDtoOtp.isPresent());
        }
    }

    @Test
    void getOfferedSkills() {
        final long userId = 2345L;
        Map<Skill, Long> sentCandidatesMap = new HashMap<>();

        Skill candidate1 = skillMapper.toEntity(new SkillDto(SKILL_ID1, SKILL_TITLE1));
        Long offersAmount1 = 2L;
        Skill candidate2 = skillMapper.toEntity(new SkillDto(SKILL_ID2, SKILL_TITLE2));
        Long offersAmount2 = 3L;
        Skill candidate3 = skillMapper.toEntity(new SkillDto(SKILL_ID3, SKILL_TITLE3));
        Long offersAmount3 = 4L;

        sentCandidatesMap.put(candidate1, offersAmount1);
        sentCandidatesMap.put(candidate2, offersAmount2);
        sentCandidatesMap.put(candidate3, offersAmount3);

        when(skillService.getOfferedSkills(userId)).thenReturn(sentCandidatesMap);
        List<SkillCandidateDto> returnedCandidatesDto = skillController.getOfferedSkills(userId);

        assertEquals(sentCandidatesMap.size(), returnedCandidatesDto.size());
        for (Map.Entry<Skill, Long> entry : sentCandidatesMap.entrySet()) {
            Optional<SkillCandidateDto> returnedSkillCandidateDtoOpt = returnedCandidatesDto.stream()
                    .filter(dto -> entry.getKey().getTitle().equals(dto.getSkill().getTitle()))
                    .findFirst();

            assertTrue(returnedSkillCandidateDtoOpt.isPresent());
            assertEquals(entry.getValue(), returnedSkillCandidateDtoOpt.get().getOffersAmount());
        }
    }

    @Test
    void acquireSkillFromOffers() {
        final long skillId = 123L;
        final long userId = 2345L;

        when(skillService.acquireSkillFromOffers(skillId, userId)).thenReturn(skill);
        SkillDto testDto = skillController.acquireSkillFromOffers(skillId, userId);

        assertEquals(skillDto, testDto);
    }
}