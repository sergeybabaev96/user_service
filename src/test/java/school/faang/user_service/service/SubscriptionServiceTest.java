package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.impl.SubscriptionServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    private static final long AUTHOR_ID = 1L;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Spy
    private RecommendationMapper recommendationMapper;

    private Recommendation recommendation;
    private RecommendationDto recommendationDto;

    @BeforeEach
    void setUp() {
        recommendation = createTestRecommendation();
        recommendationDto = createTestRecommendationDto();
    }

    @Test
    @DisplayName("Should return list of recommendations when author has recommendations")
    void shouldReturnRecommendationsWhenAuthorHasRecommendations() {
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));

        when(recommendationRepository.findAllByAuthorId(eq(AUTHOR_ID), any(Pageable.class))).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = subscriptionService.getAllGivenRecommendations(AUTHOR_ID);

        assertThat(result).isNotEmpty().hasSize(1).first().satisfies(dto -> {
            assertThat(dto.getId()).isEqualTo(recommendation.getId());
            assertThat(dto.getContent()).isEqualTo(recommendation.getContent());
        });

        verify(recommendationRepository).findAllByAuthorId(eq(AUTHOR_ID), any(Pageable.class));
        verify(recommendationMapper).toDto(recommendation);
    }

    @Test
    @DisplayName("Should return empty list when author has no recommendations")
    void shouldReturnEmptyListWhenAuthorHasNoRecommendations() {
        Page<Recommendation> emptyPage = new PageImpl<>(Collections.emptyList());
        when(recommendationRepository.findAllByAuthorId(eq(AUTHOR_ID), any(Pageable.class))).thenReturn(emptyPage);

        List<RecommendationDto> result = subscriptionService.getAllGivenRecommendations(AUTHOR_ID);

        assertThat(result).isEmpty();
        verify(recommendationRepository).findAllByAuthorId(eq(AUTHOR_ID), any(Pageable.class));
    }

    private Recommendation createTestRecommendation() {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);
        recommendation.setContent("Test Recommendation");
        return recommendation;
    }

    private RecommendationDto createTestRecommendationDto() {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(1L);
        dto.setContent("Test Recommendation");
        return dto;
    }
}