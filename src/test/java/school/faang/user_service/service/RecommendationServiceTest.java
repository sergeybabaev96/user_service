package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.SkillAcquiredEvent;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private static final Long AUTHOR_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long SKILL_ID = 1L;
    private static final Long RECOMMENDATION_ID = 1L;
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String CONTENT = "Test content";

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @InjectMocks
    private RecommendationService recommendationService;

    private User author;
    private User receiver;
    private Skill skill;
    private SkillOffer skillOffer;
    private RecommendationDto recommendationDto;

    @BeforeEach
    void setUp() {
        author = createUser(AUTHOR_ID);
        receiver = createUser(RECEIVER_ID);
        skill = createSkill(SKILL_ID);
        skillOffer = createSkillOffer(skill);
        recommendationDto = new RecommendationDto();
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setSkills(new ArrayList<>());
        return user;
    }

    private Skill createSkill(Long id) {
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }

    private SkillOffer createSkillOffer(Skill skill) {
        SkillOffer offer = new SkillOffer();
        offer.setSkill(skill);
        return offer;
    }

    private Recommendation createRecommendation(Long id, LocalDateTime createdAt, String content,
                                                List<SkillOffer> offers) {
        Recommendation rec = new Recommendation();
        rec.setId(id);
        rec.setCreatedAt(createdAt);
        rec.setContent(content);
        rec.setAuthor(author);
        rec.setReceiver(receiver);
        rec.setSkillOffers(offers);
        return rec;
    }

    private void mockCommonRecommendationSetup(Recommendation recommendation, Recommendation lastRecommendation) {
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendation);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(AUTHOR_ID, RECEIVER_ID))
                .thenReturn(Optional.ofNullable(lastRecommendation));
    }

    private void setRecommendationDto() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setSkillId(SKILL_ID);
        recommendationDto.setAuthorId(AUTHOR_ID);
        recommendationDto.setReceiverId(RECEIVER_ID);
        recommendationDto.setSkillOffers(List.of(skillOfferDto));
    }

    @Test
    void testGetAllGivenRecommendations() {
        List<Recommendation> recommendations = List.of(createRecommendation(RECOMMENDATION_ID, NOW, CONTENT,
                List.of()));
        List<RecommendationDto> dtos = List.of(recommendationDto);

        when(recommendationRepository.findAllByAuthorId(AUTHOR_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendations));
        when(recommendationMapper.toDto(recommendations)).thenReturn(dtos);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(AUTHOR_ID);
        assertEquals(dtos, result);
        verify(recommendationRepository).findAllByAuthorId(AUTHOR_ID, Pageable.unpaged());
    }

    @Test
    void testGetAllUserRecommendations() {
        List<Recommendation> recommendations = List.of(createRecommendation(RECOMMENDATION_ID, NOW,
                CONTENT, List.of()));
        List<RecommendationDto> dtos = List.of(recommendationDto);

        when(recommendationRepository.findAllByReceiverId(RECEIVER_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendations));
        when(recommendationMapper.toDto(recommendations)).thenReturn(dtos);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(RECEIVER_ID);
        assertEquals(dtos, result);
        verify(recommendationRepository).findAllByReceiverId(RECEIVER_ID, Pageable.unpaged());
    }

    @Test
    void testDelete() {
        recommendationService.delete(RECOMMENDATION_ID);
        verify(recommendationRepository).deleteById(RECOMMENDATION_ID);
    }

    @Test
    void testUpdateSuccess() {
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW, "Updated",
                List.of(skillOffer));
        Recommendation lastRec = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        mockCommonRecommendationSetup(recommendation, lastRec);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.update(recommendationDto);
        assertNotNull(result);
        verify(recommendationRepository).update(RECOMMENDATION_ID, RECEIVER_ID, recommendation.getContent());
        verify(skillOfferRepository).deleteAllByRecommendationId(RECOMMENDATION_ID);
        verify(skillOfferRepository).create(RECOMMENDATION_ID, SKILL_ID);
    }

    @Test
    void testUpdateThrowsExceptionIfLessThan6Months() {
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW.minusMonths(5), CONTENT,
                List.of(skillOffer));
        mockCommonRecommendationSetup(recommendation, recommendation);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.update(recommendationDto));
        assertEquals("the author 1 gives a recommendation 1 earlier than 6 months after his last " +
                "recommendation to this user", ex.getMessage());
    }

    @Test
    void testUpdateThrowsExceptionIfSkillDoesNotExist() {
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW, CONTENT, List.of(skillOffer));
        Recommendation lastRec = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        mockCommonRecommendationSetup(recommendation, lastRec);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.update(recommendationDto));
        assertEquals("Skill 1 does not exist", ex.getMessage());
    }

    @Test
    void testCreateSuccess() {
        setRecommendationDto();
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW, CONTENT, List.of(skillOffer));
        Recommendation lastRec = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        mockCommonRecommendationSetup(recommendation, lastRec);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(recommendationRepository.create(AUTHOR_ID, RECEIVER_ID, CONTENT)).thenReturn(RECOMMENDATION_ID);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertNotNull(result);
        verify(recommendationRepository).create(AUTHOR_ID, RECEIVER_ID, CONTENT);
        verify(skillOfferRepository).create(RECOMMENDATION_ID, SKILL_ID);
        ArgumentCaptor<SkillAcquiredEvent> eventCaptor = ArgumentCaptor.forClass(SkillAcquiredEvent.class);
        verify(skillAcquiredEventPublisher, times(1)).publish(eventCaptor.capture());

        SkillAcquiredEvent event = eventCaptor.getValue();
        assertEquals(AUTHOR_ID, event.getAuthorId());
        assertEquals(RECEIVER_ID, event.getRecipientId());
        assertEquals(SKILL_ID, event.getSkillId());
    }

    @Test
    void testCreateThrowsExceptionIfLessThan6Months() {
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW, CONTENT, List.of(skillOffer));
        Recommendation lastRec = createRecommendation(2L, NOW.minusMonths(5), "Recent", List.of());

        mockCommonRecommendationSetup(recommendation, lastRec);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("the author 1 gives a recommendation 1 earlier than " +
                "6 months after his last recommendation to this user", ex.getMessage());
    }

    @Test
    void testCreateThrowsExceptionIfNoSkillOffers() {
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW, CONTENT, Collections.emptyList());
        Recommendation lastRec = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        mockCommonRecommendationSetup(recommendation, lastRec);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("Skill offers is empty", ex.getMessage());
    }

    @Test
    void testCreateThrowsExceptionIfSkillDoesNotExist() {
        Recommendation recommendation = createRecommendation(RECOMMENDATION_ID, NOW, CONTENT, List.of(skillOffer));
        Recommendation lastRec = createRecommendation(2L, NOW.minusMonths(7), "Old", List.of());

        mockCommonRecommendationSetup(recommendation, lastRec);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("Skill 1 does not exist", ex.getMessage());
    }
}
