package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.skilloffer.SkillOfferService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private UserService userService;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    String content = "This is a recommendation.";
    private Long authorId;
    private Long receiverId;
    private User firstUser;
    private User secondUser;
    private List<SkillOfferDto> skillOffersDto;
    private List<SkillOffer> skillOffers;
    private Skill firstSkill;
    private Skill secondSkill;
    private SkillOffer firstSkillOffer;
    private SkillOffer secondSkillOffer;
    private Recommendation firstRecommendation;
    private Recommendation secondRecommendation;
    private LocalDateTime now;
    private LocalDateTime minusMonth;
    private LocalDateTime minusSevenMonth;
    private UserSkillGuarantee firstUserSkillGuarantee;
    private UserSkillGuarantee secondUserSkillGuarantee;
    private boolean exist = true;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        minusMonth = now.minusMonths(1L);
        minusSevenMonth = now.minusMonths(7L);
        authorId = 1L;
        receiverId = 2L;
        skillOffersDto = List.of(new SkillOfferDto(1L, 1L),
                new SkillOfferDto(2L, 2L));
        firstUserSkillGuarantee = UserSkillGuarantee.builder()
                .user(User.builder()
                        .id(1L)
                        .build())
                .build();
        secondUserSkillGuarantee = UserSkillGuarantee.builder()
                .user(User.builder()
                        .id(2L)
                        .build())
                .build();
        firstSkill = Skill.builder()
                .title("Java")
                .guarantees(List.of(firstUserSkillGuarantee))
                .build();
        secondSkill = Skill.builder()
                .title("Python")
                .guarantees(List.of(secondUserSkillGuarantee))
                .build();
        firstUser = User.builder()
                .id(authorId)
                .skills(List.of(firstSkill, secondSkill))
                .build();
        secondUser = User.builder()
                .id(receiverId)
                .skills(List.of(firstSkill, secondSkill))
                .build();
        firstRecommendation = Recommendation.builder()
                .author(firstUser)
                .createdAt(now)
                .skillOffers(skillOffers)
                .build();
        secondRecommendation = Recommendation.builder()
                .author(firstUser)
                .createdAt(minusMonth)
                .build();
        recommendations = List.of(firstRecommendation, secondRecommendation);
        firstSkillOffer = SkillOffer.builder()
                .skill(firstSkill)
                .recommendation(firstRecommendation)
                .build();
        secondSkillOffer = SkillOffer.builder()
                .skill(secondSkill)
                .recommendation(secondRecommendation)
                .build();
        skillOffers = List.of(firstSkillOffer, secondSkillOffer);
    }

    @Test
    void testCreateWithRecommendationCreatedBeforeSixMonth() {
        prepare();
        DataValidationException exception = assertThrows(DataValidationException.class, () -> recommendationService
                .create(authorId, receiverId, skillOffersDto, content, minusMonth));
        assertEquals("It hasn't been 6 months yet", exception.getMessage());
    }

    @Test
    void testCreateWithRecommendationCreatedAfterSixMonthAndNotExistSkillsOffer() {
        prepare();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> recommendationService
                .create(authorId, receiverId, skillOffersDto, content, minusSevenMonth));
        assertEquals("Skill Java do not exist", exception.getMessage());
    }

    @Test
    void testCreateSaveSkillOffers() {
        prepare();
        checkExistSkillsOfferOrNot(exist);

        recommendationService.create(authorId, receiverId, skillOffersDto, content, minusSevenMonth);

        verify(skillOfferService, times(skillOffers.size())).create(anyLong(), anyLong());
    }

    @Test
    void testCreateAddGuarantorIfSkillExists() {
        prepare();
        checkExistSkillsOfferOrNot(exist);
        when(userService.getUser(receiverId)).thenReturn(secondUser);

        recommendationService.create(authorId, receiverId, skillOffersDto, content, minusSevenMonth);

        verify(userSkillGuaranteeService, times(1))
                .saveUserSkillGuarantee(anyLong(), any(), anyLong());
    }

    @Test
    void testCreateSaveRecommendation() {
        prepare();
        checkExistSkillsOfferOrNot(exist);
        when(userService.getUser(receiverId)).thenReturn(secondUser);

        recommendationService.create(authorId, receiverId, skillOffersDto, content, minusSevenMonth);

        verify(recommendationRepository, times(1)).save(any());
    }

    @Test
    void testUpdateRecommendation() {
        prepare();
        checkExistSkillsOfferOrNot(exist);

        recommendationService.update(authorId, receiverId, skillOffersDto, content, minusSevenMonth);

        verify(recommendationRepository, times(1))
                .update(anyLong(), anyLong(), any());
    }

    @Test
    void testUpdateDeleteSkillOffer() {
        prepare();
        checkExistSkillsOfferOrNot(exist);

        recommendationService.update(authorId, receiverId, skillOffersDto, content, minusSevenMonth);

        verify(skillOfferService, times(1)).deleteSkillOffer(anyLong());
    }

    @Test
    void testDelete() {
        recommendationService.delete(firstRecommendation.getId());

        verify(recommendationRepository, times(1))
                .deleteById(firstRecommendation.getId());
    }

    @Test
    void testGetAllUserRecommendation() {
        recommendationService.getAllUserRecommendations(firstUser.getId());

        verify(recommendationRepository, times(1))
                .findAllByReceiverId(firstUser.getId());
    }

    @Test
    void testGetAllGivenRecommendations() {
        recommendationService.getAllGivenRecommendations(secondUser.getId());

        verify(recommendationRepository, times(1))
                .findAllByAuthorId(secondUser.getId());
    }

    private void checkExistSkillsOfferOrNot(boolean trueOrFalse) {
        skillOffers.forEach(skillOffer -> when(skillService.skillExistsByTitle(skillOffer
                .skill.getTitle())).thenReturn(trueOrFalse));
    }

    private void prepare() {
        when(userService.getUser(firstUser.getId())).thenReturn(firstUser);
        when(userService.getUser(secondUser.getId())).thenReturn(secondUser);
        when(skillOfferService.getSkillOffers(skillOffersDto, secondUser.getId()))
                .thenReturn(skillOffers);
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L,
                2L)).thenReturn(Optional.of(firstRecommendation));
    }
}
