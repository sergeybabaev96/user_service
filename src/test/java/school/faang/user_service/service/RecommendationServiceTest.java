package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.impl.RecommendationServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    private static final Long TEST_ID = 1L;
    private static final Long TEST_AUTHOR_ID = 2L;
    private static final Long TEST_RECEIVER_ID = 3L;
    private static final Long TEST_SKILL_ID = 4L;
    private static final String TEST_CONTENT = "Test recommendation content";
    private static final String BLANK_CONTENT = Strings.EMPTY;
    private static final int TEST_LIST_SIZE = 1;
    private static final String ERROR_MESSAGE_BLANK_CONTENT = "Recommendation content is blank";
    private static final String ERROR_MESSAGE_SKILLS_NOT_EXIST = "These skills do not exists in system";

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private RecommendationMapper recommendationMapper;

    @Test
    void createRecommendation_WithBlankContent_ShouldThrowException() {
        RecommendationDto recommendationDto = createRecommendationDto(BLANK_CONTENT);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(ERROR_MESSAGE_BLANK_CONTENT, exception.getMessage());
    }

    @Test
    void createRecommendation_WithNonExistingSkills_ShouldThrowException() {
        RecommendationDto recommendationDto = prepareRecommendationWithSkills(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recommendationService.create(recommendationDto));

        assertEquals(ERROR_MESSAGE_SKILLS_NOT_EXIST, exception.getMessage());
    }

    @Test
    void createRecommendation_WithValidData_ShouldSucceed() {
        RecommendationDto recommendationDto = prepareRecommendationWithSkills(true);
        when(recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()))
                .thenReturn(TEST_ID);

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
    }

    @Test
    void deleteRecommendation_WhenExists_ShouldSucceed() {
        recommendationService.delete(TEST_ID);
        verify(recommendationRepository).deleteById(TEST_ID);
    }

    @Test
    void updateRecommendation_WithBlankContent_ShouldThrowException() {
        RecommendationDto recommendationDto = createRecommendationDto(BLANK_CONTENT);

        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendationDto));
    }

    @Test
    void updateRecommendation_WithValidData_ShouldSucceed() {
        RecommendationDto recommendationDto = prepareRecommendationWithSkills(true);
        recommendationDto.setId(TEST_ID);

        RecommendationDto result = recommendationService.update(recommendationDto);

        assertEquals(recommendationDto, result);
        verify(skillOfferRepository).deleteAllByRecommendationId(recommendationDto.getId());
    }

    @Test
    void getAllUserRecommendations_ShouldReturnList() {
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(
                Recommendation.builder()
                        .id(TEST_ID)
                        .content(TEST_CONTENT)
                        .build()
        ));
        when(recommendationRepository.findAllByReceiverId(eq(TEST_RECEIVER_ID), any(Pageable.class)))
                .thenReturn(recommendationPage);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(TEST_RECEIVER_ID);

        assertNotNull(result);
        assertEquals(TEST_LIST_SIZE, result.size());
        verify(recommendationMapper).toDto(any(Recommendation.class));
    }

    @Test
    void getAllUserRecommendations_WhenEmpty_ShouldReturnEmptyList() {
        Page<Recommendation> emptyPage = new PageImpl<>(Collections.emptyList());
        when(recommendationRepository.findAllByReceiverId(eq(TEST_RECEIVER_ID), any(Pageable.class)))
                .thenReturn(emptyPage);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(TEST_RECEIVER_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private RecommendationDto createRecommendationDto(String content) {
        RecommendationDto dto = new RecommendationDto();
        dto.setAuthorId(TEST_AUTHOR_ID);
        dto.setReceiverId(TEST_RECEIVER_ID);
        dto.setContent(content);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }

    private RecommendationDto prepareRecommendationWithSkills(boolean skillExists) {
        RecommendationDto dto = createRecommendationDto(TEST_CONTENT);
        dto.setSkillOffers(List.of(SkillOfferDto.builder()
                .skillId(TEST_SKILL_ID)
                .build()));
        when(skillRepository.existsById(TEST_SKILL_ID)).thenReturn(skillExists);
        return dto;
    }
}