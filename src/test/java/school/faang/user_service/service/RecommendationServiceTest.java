package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationViewDto;
import school.faang.user_service.dto.skilloffer.SkillOfferCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.RecommendationValidator;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private RecommendationValidator recommendationValidator;

    @InjectMocks
    private RecommendationService recommendationService;

    private User author;
    private User receiver;
    private Long recommendationCreateDtoId;
    private RecommendationCreateDto recommendationCreateDto;
    private RecommendationViewDto recommendationViewDto;
    private Recommendation recommendationEntity;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);

        receiver = new User();
        receiver.setId(2L);

        recommendationCreateDto = new RecommendationCreateDto();
        recommendationCreateDto.setAuthorId(author.getId());
        recommendationCreateDto.setReceiverId(receiver.getId());
        recommendationCreateDto.setSkillOffers(List.of(new SkillOfferCreateDto()));
        recommendationCreateDto.setContent("Create content");
        recommendationCreateDtoId = 1L;

        recommendationViewDto = new RecommendationViewDto();

        recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        recommendationEntity.setAuthor(author);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent("Create content");
    }

    @DisplayName("Проверка успешного создания рекомендации")
    @Test
    void createRecommendationWithValidInputsTest() {
        Mockito.when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        Mockito.when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));
        Mockito.doReturn(recommendationEntity)
                .when(recommendationMapper).createDtoToEntity(recommendationCreateDto);
        Mockito.doReturn(recommendationViewDto)
                .when(recommendationMapper).toViewDto(recommendationEntity);

        RecommendationViewDto result = recommendationService
                .create(recommendationCreateDto, recommendationCreateDtoId);

        Assertions.assertNotNull(result);

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .save(recommendationEntity);
    }

    @DisplayName("Проверка получения ошибки при отсутствии получателя")
    @Test
    void createRecommendationWithInvalidReceiver() {
        Mockito.when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
             recommendationService.create(recommendationCreateDto, recommendationCreateDtoId));

        Assertions.assertTrue(exception.getMessage().contains("User is not found"));
    }

    @DisplayName("Проверка получения ошибки при отсутствии автора")
    @Test
    void createRecommendationWithInvalidAuthor() {
        Mockito.when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));
        Mockito.when(userRepository.findById(author.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            recommendationService.create(recommendationCreateDto, recommendationCreateDtoId));

        Assertions.assertTrue(exception.getMessage().contains("User is not found"));
    }

    @DisplayName("Проверка успешного обновления рекомендации")
    @Test
    void updateRecommendationWithValidInputsTest() {
        Mockito.when(recommendationRepository.findById(recommendationCreateDtoId))
                .thenReturn(Optional.of(recommendationEntity));
        Mockito.when(recommendationMapper.toViewDto(recommendationEntity))
                .thenReturn(recommendationViewDto);

        RecommendationViewDto result = recommendationService
                .update(recommendationCreateDto, recommendationCreateDtoId);

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .update(recommendationCreateDtoId
                        ,recommendationCreateDto.getReceiverId()
                        ,recommendationCreateDto.getContent());
        Mockito.verify(skillOfferRepository, Mockito.times(1))
                        .deleteAllByRecommendationId(recommendationCreateDtoId);
        Mockito.verify(recommendationRepository, Mockito.times(1))
                        .findById(recommendationCreateDtoId);
        Mockito.verify(recommendationMapper, Mockito.times(1))
                        .toViewDto(recommendationEntity);

        Assertions.assertNotNull(result);
    }

    @DisplayName("Проверка получения ошибки при обновлении несуществующей рекомендации")
    @Test
    void updateRecommendationWithInvalidIdTest() {
        Mockito.when(recommendationRepository.findById(recommendationCreateDtoId))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            recommendationService.update(recommendationCreateDto, recommendationCreateDtoId));

        Assertions.assertTrue(exception.getMessage().contains("Recommendation not found"));
    }

    @DisplayName("Проверка успешного удаления рекомендации")
    @Test
    void deleteRecommendationWithValidInputsTest() {
        Mockito.when(recommendationRepository.existsById(recommendationCreateDtoId))
                .thenReturn(true);

        recommendationService.delete(recommendationCreateDtoId);

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .deleteById(recommendationCreateDtoId);
    }

    @DisplayName("Проверка получения ошибки при удалении несуществующей рекомендации")
    @Test
    void deleteRecommendationWithInvalidInputsTest() {
        Mockito.when(recommendationRepository.existsById(recommendationCreateDtoId))
                .thenReturn(false);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            recommendationService.delete(recommendationCreateDtoId));

        Assertions.assertTrue(exception.getMessage()
                .contains(String.format("Recommendation id %d not found", recommendationCreateDtoId)));
    }

    @DisplayName("Проверка успешного получения всех рекомендаций для пользователя")
    @Test
    void getAllUserRecommendationsTest() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Recommendation> recommendations = List.of(recommendationEntity);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);

        Mockito.when(recommendationRepository.findAllByReceiverId(receiver.getId(),pageable))
                .thenReturn(recommendationPage);
        Mockito.when(recommendationMapper.toViewDto(recommendationEntity))
                .thenReturn(recommendationViewDto);

        Page<RecommendationViewDto> result = recommendationService.getAllUserRecommendations(receiver.getId(),pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findAllByReceiverId(receiver.getId(),pageable);
        Mockito.verify(recommendationMapper, Mockito.times(1))
                .toViewDto(recommendationEntity);
    }

    @DisplayName("Проверка успешного получения всех созданных рекомендаций")
    @Test
    void getAllCreatedRecommendationTest() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Recommendation> recommendations = List.of(recommendationEntity);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);

        Mockito.when(recommendationRepository.findAllByAuthorId(author.getId(),pageable))
                .thenReturn(recommendationPage);
        Mockito.when(recommendationMapper.toViewDto(recommendationEntity))
                .thenReturn(recommendationViewDto);

        Page<RecommendationViewDto> result = recommendationService.getAllCreatedRecommendation(author.getId(), pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify(recommendationRepository, Mockito.times(1))
                .findAllByAuthorId(author.getId(),pageable);
        Mockito.verify(recommendationMapper, Mockito.times(1))
                .toViewDto(recommendationEntity);
    }
}
