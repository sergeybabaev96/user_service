package school.faang.user_service.service.recommendation;

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
import school.faang.user_service.publisher.RedisEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skilloffer.SkillOfferService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.recommendation.RecommendationValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock
    private RedisEventPublisher redisEventPublisher;
    @Mock
    private UserService userService;


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
        when(userService.getUserEntity(author.getId())).thenReturn(author);
        when(userService.getUserEntity(receiver.getId())).thenReturn(receiver);
        when(recommendationMapper.createDtoToEntity(recommendationCreateDto))
                .thenReturn(recommendationEntity);
        when(recommendationRepository.save(recommendationEntity))
                .thenReturn(recommendationEntity);
        when(recommendationMapper.toViewDto(recommendationEntity))
                .thenReturn(recommendationViewDto);

        RecommendationViewDto result = recommendationService
                .create(recommendationCreateDto, recommendationCreateDtoId);

        assertNotNull(result);
        assertEquals(recommendationViewDto, result);

        verify(recommendationRepository, times(1))
                .save(recommendationEntity);
    }

    @DisplayName("Проверка получения ошибки при отсутствии получателя")
    @Test
    void createRecommendationWithInvalidReceiver() {
        when(userService.getUserEntity(receiver.getId())).thenReturn(receiver);
        when(userService.getUserEntity(author.getId()))
                .thenThrow(new EntityNotFoundException("User is not found"));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
             recommendationService.create(recommendationCreateDto, recommendationCreateDtoId));

        assertTrue(exception.getMessage().contains("User is not found"));

        verify(recommendationValidator).validate(recommendationCreateDto);
        verify(userService).getUserEntity(receiver.getId());
        verify(userService).getUserEntity(author.getId());
    }

    @DisplayName("Проверка получения ошибки при отсутствии автора")
    @Test
    void createRecommendationWithInvalidAuthor() {
        when(userService.getUserEntity(receiver.getId())).thenReturn(receiver);
        when(userService.getUserEntity(author.getId()))
                .thenThrow(new EntityNotFoundException("User is not found"));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            recommendationService.create(recommendationCreateDto, recommendationCreateDtoId));

        assertTrue(exception.getMessage().contains("User is not found"));

        verify(recommendationValidator).validate(recommendationCreateDto);
        verify(userService).getUserEntity(receiver.getId());
        verify(userService).getUserEntity(author.getId());
    }

    @DisplayName("Проверка успешного обновления рекомендации")
    @Test
    void updateRecommendationWithValidInputsTest() {
        when(recommendationRepository.findById(recommendationCreateDtoId))
                .thenReturn(Optional.of(recommendationEntity));
        when(recommendationMapper.toViewDto(recommendationEntity))
                .thenReturn(recommendationViewDto);

        RecommendationViewDto result = recommendationService
                .update(recommendationCreateDto, recommendationCreateDtoId);

        verify(recommendationRepository, times(1))
                .update(recommendationCreateDtoId
                        ,recommendationCreateDto.getReceiverId()
                        ,recommendationCreateDto.getContent());
        verify(skillOfferRepository, times(1))
                        .deleteAllByRecommendationId(recommendationCreateDtoId);
        verify(recommendationRepository, times(1))
                        .findById(recommendationCreateDtoId);
        verify(recommendationMapper, times(1))
                        .toViewDto(recommendationEntity);

        assertNotNull(result);
    }

    @DisplayName("Проверка получения ошибки при обновлении несуществующей рекомендации")
    @Test
    void updateRecommendationWithInvalidIdTest() {
        when(recommendationRepository.findById(recommendationCreateDtoId))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            recommendationService.update(recommendationCreateDto, recommendationCreateDtoId));

        assertTrue(exception.getMessage().contains("Recommendation not found"));
    }

    @DisplayName("Проверка успешного удаления рекомендации")
    @Test
    void deleteRecommendationWithValidInputsTest() {
        when(recommendationRepository.existsById(recommendationCreateDtoId))
                .thenReturn(true);

        recommendationService.delete(recommendationCreateDtoId);

        verify(recommendationRepository, times(1))
                .deleteById(recommendationCreateDtoId);
    }

    @DisplayName("Проверка получения ошибки при удалении несуществующей рекомендации")
    @Test
    void deleteRecommendationWithInvalidInputsTest() {
        when(recommendationRepository.existsById(recommendationCreateDtoId))
                .thenReturn(false);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            recommendationService.delete(recommendationCreateDtoId));

        assertTrue(exception.getMessage()
                .contains(String.format("Recommendation id %d not found", recommendationCreateDtoId)));
    }

    @DisplayName("Проверка успешного получения всех рекомендаций для пользователя")
    @Test
    void getAllUserRecommendationsTest() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Recommendation> recommendations = List.of(recommendationEntity);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);

        when(recommendationRepository.findAllByReceiverId(receiver.getId(),pageable))
                .thenReturn(recommendationPage);
        when(recommendationMapper.toViewDto(recommendationEntity))
                .thenReturn(recommendationViewDto);

        Page<RecommendationViewDto> result = recommendationService.getAllUserRecommendations(receiver.getId(),pageable);

        assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        verify(recommendationRepository, times(1))
                .findAllByReceiverId(receiver.getId(),pageable);
        verify(recommendationMapper, times(1))
                .toViewDto(recommendationEntity);
    }

    @DisplayName("Проверка успешного получения всех созданных рекомендаций")
    @Test
    void getAllCreatedRecommendationTest() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Recommendation> recommendations = List.of(recommendationEntity);
        Page<Recommendation> recommendationPage = new PageImpl<>(recommendations);

        when(recommendationRepository.findAllByAuthorId(author.getId(),pageable))
                .thenReturn(recommendationPage);
        when(recommendationMapper.toViewDto(recommendationEntity))
                .thenReturn(recommendationViewDto);

        Page<RecommendationViewDto> result = recommendationService.getAllCreatedRecommendation(author.getId(), pageable);
        assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        verify(recommendationRepository, times(1))
                .findAllByAuthorId(author.getId(),pageable);
        verify(recommendationMapper, times(1))
                .toViewDto(recommendationEntity);
    }
}
