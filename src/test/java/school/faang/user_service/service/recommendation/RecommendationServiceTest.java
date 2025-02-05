package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @InjectMocks
    private RecommendationServiceImpl recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository guaranteeRepository;
    @Mock
    private RecommendationMapper recommendationMapper;

    @Test
    public void testCreateRecommendationWithOfferInValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(12L, 14L))
                .thenReturn(Optional.of(getRecommendationInvalidCreatedDate()));

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(getRecommendationDtoWithOffer()));

        Mockito.verify(recommendationRepository, Mockito.times(0))
                .create(12L, 14L, "content");
    }

    @Test
    public void testCreateRecommendationWithOfferValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(12L, 14L))
                .thenReturn(Optional.of(getRecommendationValidCreatedDate()));
        recommendationService.create(getRecommendationDtoWithNullableOffer());
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .create(12L, 14L, "content");
    }

    @Test
    public void testCreateRecommendationWithOutOfferValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                12L, 14L
        )).thenReturn(Optional.empty());
        recommendationService.create(getRecommendationDtoWithNullableOffer());
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .create(12L, 14L, "content");
    }

    @Test
    public void testCreateRecommendationWithOfferNotExistSkillsInSystemInValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                12L, 14L
        )).thenReturn(Optional.of(getRecommendationValidCreatedDate()));

        Mockito.when(skillRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(getRecommendationDtoWithOffer()));
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .create(12L, 14L, "content");
        Mockito.verify(skillOfferRepository, Mockito.times(0))
                .create(15L, 1L);
    }

    @Test
    public void testCreateRecommendationWithOfferExistSkillsInSystemValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                12L, 14L
        )).thenReturn(Optional.of(getRecommendationValidCreatedDate()));

        Mockito.when(skillRepository.findAll()).thenReturn(getSkillsSystem());

        recommendationService.create(getRecommendationDtoWithOffer());
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .create(12L, 14L, "content");
        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .create(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testCreateRecommendationWithOfferGuaranteeExistValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                12L, 14L
        )).thenReturn(Optional.of(getRecommendationValidCreatedDate()));

        Mockito.when(skillRepository.findAll()).thenReturn(getSkillsSystem());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(15L, 14L))
                .thenReturn(skillOffers());
        Mockito.when(guaranteeRepository.findAll()).thenReturn(getUserSkillGuarantees());

        recommendationService.create(getRecommendationDtoWithOffer());
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .create(12L, 14L, "content");
        Mockito.verify(skillOfferRepository, Mockito.times(0))
                .create(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(guaranteeRepository, Mockito.times(0))
                .create(14L, 15L, 12L);
    }

    @Test
    public void testCreateRecommendationWithOfferAddGuaranteeValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                12L, 14L
        )).thenReturn(Optional.of(getRecommendationValidCreatedDate()));

        Mockito.when(skillRepository.findAll()).thenReturn(getSkillsSystem());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(15L, 14L))
                .thenReturn(skillOffers());
        Mockito.when(guaranteeRepository.findAll()).thenReturn(Collections.emptyList());

        recommendationService.create(getRecommendationDtoWithOffer());
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .create(12L, 14L, "content");
        Mockito.verify(skillOfferRepository, Mockito.times(0))
                .create(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(guaranteeRepository, Mockito.times(1))
                .create(14L, 15L, 12L);

    }

    @Test
    public void testUpdateRecommendationWithOfferRecommendationIdInValid() {
        assertThrows(DataValidationException.class,
                () -> recommendationService.update(getRecommendationDtoWithNullableOffer()));

        Mockito.verify(recommendationRepository, Mockito.times(0))
                .update(12L, 14L, "content");
    }

    @Test
    public void testUpdateRecommendationWithOfferValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(12L, 14L))
                .thenReturn(Optional.of(getRecommendationValidCreatedDate()));
        RecommendationDto recommendation = getRecommendationDtoWithNullableOffer();
        recommendation.setId(4L);
        recommendationService.update(recommendation);
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .update(12L, 14L, "content");
    }

    @Test
    public void testUpdateRecommendationWithOfferDeleteByRecommendationValid() {

        Mockito.when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(12L, 14L))
                .thenReturn(Optional.of(getRecommendationValidCreatedDate()));
        RecommendationDto recommendation = getRecommendationDtoWithNullableOffer();
        recommendation.setId(4L);
        recommendationService.update(recommendation);
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .update(12L, 14L, "content");
        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .deleteAllByRecommendationId(recommendation.getId());

    }

    @Test
    public void testDeleteRecommendationValid() {
        recommendationService.delete(5L);
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .deleteById(5L);
    }

    @Test
    public void testGetRecommendationByReceiverValid() {
        Mockito.when(recommendationRepository.findAllByReceiverId(
                5L, PageRequest.of(0, 100)
        )).thenReturn(createPageFromList(List.of(getRecommendationValidCreatedDate()), 0, 100));
        recommendationService.getAllUserRecommendations(5L, 0, 100);
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findAllByReceiverId(5L, PageRequest.of(0, 100));
    }

    @Test
    public void testGetRecommendationByReceiverNotValid() {

        recommendationService.getAllUserRecommendations(5L, 0, 100);
        Mockito.verify(recommendationMapper, Mockito.times(0))
                .toDto(null);
    }

    @Test
    public void testGetRecommendationByAuthorValid() {
        Mockito.when(recommendationRepository.findAllByAuthorId(
                5L, PageRequest.of(0, 100)
        )).thenReturn(createPageFromList(List.of(getRecommendationValidCreatedDate()), 0, 100));
        recommendationService.getAllGivenRecommendations(5L, 0, 100);
        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findAllByAuthorId(5L, PageRequest.of(0, 100));
    }

    @Test
    public void testGetRecommendationByAuthorNotValid() {

        recommendationService.getAllGivenRecommendations(5L, 0, 100);
        Mockito.verify(recommendationMapper, Mockito.times(0))
                .toDto(null);
    }

    private RecommendationDto getRecommendationDtoWithNullableOffer() {
        return RecommendationDto.builder()
                .content("content")
                .authorId(12L)
                .receiverId(14L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private RecommendationDto getRecommendationDtoWithOffer() {
        RecommendationDto recommendationDto = getRecommendationDtoWithNullableOffer();
        recommendationDto.setSkillOffers(skillOffersDto());
        return recommendationDto;
    }

    private List<SkillOfferDto> skillOffersDto() {
        return List.of(SkillOfferDto.builder().id(18L).skillId(15L).title("java").build());
    }

    private List<SkillOffer> skillOffers() {
        return List.of(SkillOffer.builder().skill(Skill.builder().id(15L).title("java").build())
                .recommendation(getRecommendationValidCreatedDate()).build());
    }

    private Recommendation getRecommendationInvalidCreatedDate() {
        return new Recommendation(5L, "old content", getAuthorUser(), getReceiverUser(),
                null, null, LocalDateTime.now().minusMonths(2), null);
    }

    private Recommendation getRecommendationValidCreatedDate() {
        return new Recommendation(5L, "old content", getAuthorUser(), getReceiverUser(),
                null, null, LocalDateTime.now().minusMonths(6), null);
    }

    private User getAuthorUser() {
        return User.builder().id(12L).build();
    }

    private User getReceiverUser() {
        return User.builder().id(14L).build();
    }

    private List<Skill> getSkillsSystem() {
        return List.of(Skill.builder().id(15L).title("java").build(),
                Skill.builder().id(16L).title("spring").build());
    }

    private List<UserSkillGuarantee> getUserSkillGuarantees() {
        return List.of(UserSkillGuarantee.builder().guarantor(getAuthorUser())
                .skill(Skill.builder().id(15L).title("java").build())
                .user(getReceiverUser()).build());
    }

    private Page<Recommendation> createPageFromList(List<Recommendation> recommendations, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recommendations.size());
        List<Recommendation> subList = recommendations.subList(start, end);
        return new PageImpl<>(subList, pageable, recommendations.size());
    }
}
