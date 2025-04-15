package school.faang.user_service.service.skilloffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SkillOfferServiceTest {
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillOfferService skillOfferService;

    private RecommendationCreateDto recommendationCreateDto;
    private SkillOfferCreateDto skillOfferCreateDto;
    private Skill skill;
    private User author;
    private User receiver;
    private long skillId;
    private Long recommendationCreateDtoId;
    private UserSkillGuarantee guarantee;
    private List<UserSkillGuarantee> skillGuarantees;

    @BeforeEach
    public void setUp() {
        guarantee = new UserSkillGuarantee();
        skillGuarantees = new ArrayList<>();
        skillGuarantees.add(guarantee);

        skill = new Skill();
        skill.setId(3L);
        skill.setGuarantees(skillGuarantees);

        author = new User();
        author.setId(1L);

        receiver = new User();
        receiver.setId(2L);
        receiver.setSkills(List.of(skill));

        skillOfferCreateDto = new SkillOfferCreateDto();
        skillId = 1L;
        skillOfferCreateDto.setSkillId(skillId);

        recommendationCreateDto = new RecommendationCreateDto();
        recommendationCreateDto.setAuthorId(author.getId());
        recommendationCreateDto.setReceiverId(receiver.getId());
        recommendationCreateDto.setSkillOffers(List.of(skillOfferCreateDto));
        recommendationCreateDtoId = 1L;
    }

    @DisplayName("Проверка успешного сохранения SkillOffer")
    @Test
    void saveSkillsOfferTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        Mockito.when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));
        Mockito.when(skillRepository.findById(skillOfferCreateDto.getSkillId()))
                .thenReturn(Optional.of(skill));

        skillOfferService.saveSkillsOffer(recommendationCreateDto, recommendationCreateDtoId);

        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .create(skillOfferCreateDto.getSkillId(), recommendationCreateDtoId);
        Mockito.verify(skillRepository, Mockito.times(1))
                .save(skill);
    }

    @DisplayName("Проверка получения ошибки при отсутствии SkillOffers")
    @Test
    void saveSkillsOfferWhenSkillOffersIsNullTest() {
        recommendationCreateDto.setSkillOffers(null);

        DataValidationException exception = Assertions.assertThrows(DataValidationException.class, () ->
                skillOfferService.saveSkillsOffer(recommendationCreateDto, recommendationCreateDtoId));

        Assertions.assertEquals("skillOffers is not found", exception.getMessage());
    }

    @DisplayName("Проверка получения ошибки при пустом списке SkillOffers")
    @Test
    void saveSkillsOfferWhenSkillOffersIsEmptyTest() {
        recommendationCreateDto.setSkillOffers(Collections.emptyList());

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
            skillOfferService.saveSkillsOffer(recommendationCreateDto, recommendationCreateDtoId));

        Assertions.assertEquals("skillOffers is Empty", exception.getMessage());
    }

    @DisplayName("Проверка получения ошибки при отсутствии пользователя")
    @Test
    void saveSkillsOfferWhenUserNotFoundTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () ->
            skillOfferService.saveSkillsOffer(recommendationCreateDto, recommendationCreateDtoId));
    }

    @DisplayName("Проверка получения ошибки при отсутствии Skill")
    @Test
    void saveSkillsOfferWhenSkillNotFoundTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        Mockito.when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));
        Mockito.when(skillRepository.findById(skillOfferCreateDto.getSkillId()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(DataValidationException.class, () ->
            skillOfferService.saveSkillsOffer(recommendationCreateDto, recommendationCreateDtoId));

        Assertions.assertEquals("Skill not found", exception.getMessage());
    }

    @DisplayName("Проверка, что автор не добавляется как гарант, если уже является им")
    @Test
    void saveSkillsOfferWhenGuarantorIsNotAddedTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        Mockito.when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));
        Mockito.when(skillRepository.findById(skillOfferCreateDto.getSkillId()))
                .thenReturn(Optional.of(skill));
        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        guarantee.setGuarantor(author);
        skillGuarantees.add(guarantee);

        skillOfferService.saveSkillsOffer(recommendationCreateDto, recommendationCreateDtoId);

        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .create(skillOfferCreateDto.getSkillId(), recommendationCreateDtoId);
        Mockito.verify(skillRepository, Mockito.never()).save(skill);
    }
}
