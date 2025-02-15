package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidationTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    RecommendationValidation recommendationValidation;

    @Test
    public void testTextAvailabilityWithEmptyContentM1(){
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(1L).receiverId(2L).content("")
                .build();

        assertThrows(DataValidationException.class, () -> recommendationValidation.textAvailability(recommendationDto),
                "Не отработал метод textAvailability - isEmpty()");
    }

    @Test
    public void testTextAvailabilityWithContentM1(){
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(1L).receiverId(2L).content("Контент")
                .build();
        assertDoesNotThrow(() -> recommendationValidation.checkRecommendationInterval(recommendationDto));
    }

    @Test
    public void testCheckRecommendationIntervalNullM2() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(1L).receiverId(2L)
                .build();
        when(recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId())).thenReturn(null);
        assertEquals(null, recommendationDto);
    }


    @Test
    public void testCheckRecommendationIntervalBadTimeM2() {
        LocalDateTime newDate = LocalDateTime.of(2025, Month.FEBRUARY, 7,
                21, 51, 48, 311_000_000);
        LocalDateTime oldDate = LocalDateTime.of(2025, Month.FEBRUARY, 6,
                21, 51, 48, 311_000_000);

        RecommendationDto dto = setRecommendationDto(newDate);
        Recommendation lastRecommendation = setRecommendation(oldDate);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(lastRecommendation));

        assertThrows(DataValidationException.class,
                () -> recommendationValidation.checkRecommendationInterval(dto)
        );
    }

    @Test
    public void testCheckRecommendationIntervalGoodTimeM2() {
        LocalDateTime newDate = LocalDateTime.of(2033, Month.FEBRUARY, 7,
                21, 51, 48, 311_000_000);
        LocalDateTime oldDate = LocalDateTime.of(2025, Month.FEBRUARY, 6,
                21, 51, 48, 311_000_000);

        RecommendationDto dto = setRecommendationDto(newDate);
        Recommendation lastRecommendation = setRecommendation(oldDate);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(lastRecommendation));

        assertDoesNotThrow(() -> recommendationValidation.checkRecommendationInterval(dto));
    }

    @Test
    public void testCheckingSkillsAllSkillsExistReturnsTruesM3(){
        RecommendationDto dto = createDto(List.of(1L, 2L));
        List<Skill> existingSkills = List.of(
                createSkill(1L),
                createSkill(2L),
                createSkill(3L)
        );

        when(skillRepository.findAll()).thenReturn(existingSkills);

        assertTrue(recommendationValidation.checkingSkills(dto));
    }

    @Test
    public void testCheckingSkillsSkillNotExistsThrowsExceptionM3(){
        RecommendationDto dto = createDto(List.of(1L, 4L));
        List<Skill> existingSkills = List.of(
                createSkill(1L),
                createSkill(2L),
                createSkill(3L)
        );

        when(skillRepository.findAll()).thenReturn(existingSkills);

        assertThrows(DataValidationException.class,
                () -> recommendationValidation.checkingSkills(dto)
        );
    }

    @Test
    public void testCheckingSkillsEmptyRecommendedSkillsReturnsTrue() {
        RecommendationDto dto = createDto(List.of());

        assertTrue(recommendationValidation.checkingSkills(dto));
    }

    @Test
    public void testCheckingSkillsNoSkillsInDbThrowsException() {
        RecommendationDto dto = createDto(List.of(1L, 4L));
        List<Skill> existingSkills = List.of();

        when(skillRepository.findAll()).thenReturn(existingSkills);

        assertThrows(DataValidationException.class, () -> recommendationValidation.checkingSkills(dto));
    }

    private RecommendationDto setRecommendationDto(LocalDateTime newDate){
        return RecommendationDto.builder()
                .authorId(1L).receiverId(2L).createdAt(newDate)
                .build();
    }

    private Recommendation setRecommendation(LocalDateTime oldDate){
        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(oldDate);
        return lastRecommendation;
    }

    private RecommendationDto createDto(List<Long> skillIds) {
        List<SkillOfferDto> skillOffers = skillIds.stream()
                .map(id -> SkillOfferDto.builder().id(id).build())
                .toList();

        return RecommendationDto.builder()
                .skillOffers(skillOffers)
                .build();
    }

    private Skill createSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }
}
