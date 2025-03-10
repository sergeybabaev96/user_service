package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestRecommendationService {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Spy
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    private Recommendation recommendation;


    @BeforeEach
    void setUp() {
        User author = new User();
        author.setId(0L);
        User receiver = new User();
        receiver.setId(0L);
        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setSkill(new Skill(0L,"",null,null,null,
                null, null, null));
        recommendation = new Recommendation(0L,"ValidContent", author, receiver, List.of(skillOffer),
                null, null,null);
    }
    @Test
    public void testGiveRecommendationDuringRestrictionPeriod() {
        recommendation.setUpdatedAt(LocalDateTime.now());
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(Optional.of(recommendation));

        Assert.assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendation));
    }

    @Test
    public void testGiveRecommendationWithInvalidSkill() {
        when(skillRepository.existsByTitle(Mockito.anyString())).thenReturn(false);

        Assert.assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendation));

    }

    @Test
    public void testGiveRecommendationWithValidDataShouldBeNoDataValidationException() {
        recommendation.setUpdatedAt(LocalDateTime.now().minusMonths(10));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(Optional.of(recommendation));
        when(skillRepository.existsByTitle(Mockito.anyString())).thenReturn(true);

        DataValidationException exception = new DataValidationException("NotThrown");
        try {
            recommendationService.create(recommendation);
        } catch (DataValidationException e) {
            exception = e;
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        Assertions.assertEquals("NotThrown", exception.getMessage());
    }

    @Test
    public void testGiveRecommendationReturnsValidData() {
        recommendation.setUpdatedAt(LocalDateTime.now().minusMonths(10));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(Optional.of(recommendation));
        when(skillRepository.existsByTitle(Mockito.anyString())).thenReturn(true);

        when(recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent())).thenReturn(1L);

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        Recommendation recommendationResult = recommendationService.create(recommendation);

        Assertions.assertEquals(
                recommendation.getContent(),
                recommendationResult.getContent(),
                "returned recommendation is not equal to desired");
    }

    @Test
    public void testUpdateRecommendationRepositoryUpdateMethodIsLaunched() {
        recommendation.setUpdatedAt(LocalDateTime.now().minusMonths(10));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(Optional.of(recommendation));
        when(skillRepository.existsByTitle(Mockito.anyString())).thenReturn(true);
        when(recommendationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(recommendation));
        recommendationService.update(recommendation);

        Mockito.verify(recommendationRepository, Mockito.times(1)).updateByRecommendationId(
                recommendation.getId(),
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent());
    }

    @Test
    public void testUpdateRecommendationMapperReturnsRecommendation() {
        recommendation.setUpdatedAt(LocalDateTime.now().minusMonths(10));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(Optional.of(recommendation));
        when(skillRepository.existsByTitle(Mockito.anyString())).thenReturn(true);
        when(recommendationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(recommendation));
        Recommendation recommendationResult = recommendationService.update(recommendation);

        Assertions.assertEquals(
                recommendation.getContent(),
                recommendationResult.getContent(),
                "returned recommendation is not equal to desired");
    }

    @Test
    public void testDeleteWithInvalidId() {
        when(recommendationRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assert.assertThrows(DataValidationException.class,
                () -> recommendationService.delete(Mockito.anyLong()));
    }

    @Test
    public void testDeleteWithValidId() {
        when(recommendationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(recommendation));
        recommendationService.delete(Mockito.anyLong());

        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }
}
