package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.dto.analytic.RecommendationAnalyticDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KafkaTemplate<String, RecommendationAnalyticDto> kafkaTemplate;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private RecommendationValidator recommendationValidator;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @Captor
    private ArgumentCaptor<UserSkillGuarantee> guaranteeCaptor;
    @Captor
    private ArgumentCaptor<Recommendation> recommendationCaptor;

    @Test
    public void create_ShouldCreateRecommendationSuccessfully() {
        String kafkaTopic = "recommendation-topic";
        User author = new User();
        User receiver = new User();
        author.setId(1L);
        receiver.setId(2L);

        CreateRecommendationRequest createRequest = CreateRecommendationRequest.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("content")
                .skillIds(List.of(1L, 2L))
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(author);
        when(userRepository.getReferenceById(2L)).thenReturn(receiver);
        when(recommendationRepository.create(author.getId(), receiver.getId(), createRequest.getContent()))
                .thenReturn(1L);

        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        secondSkill.setId(2L);

        when(skillRepository.findAllById(createRequest.getSkillIds()))
                .thenReturn(List.of(firstSkill, secondSkill));
        when(skillOfferRepository.create(firstSkill.getId(), 1L))
                .thenReturn(1L);
        when(skillOfferRepository.create(secondSkill.getId(), 1L))
                .thenReturn(2L);

        SkillOffer firstSkillOffer = new SkillOffer();
        SkillOffer secondSkillOffer = new SkillOffer();
        firstSkillOffer.setId(1L);
        secondSkillOffer.setId(2L);

        when(skillOfferRepository.findById(1L))
                .thenReturn(Optional.of(firstSkillOffer));
        when(skillOfferRepository.findById(2L))
                .thenReturn(Optional.of(secondSkillOffer));

        when(kafkaTemplate.send(any(), any())).thenReturn(mock(CompletableFuture.class));

        final CreateRecommendationResponse createResponse = recommendationService.create(createRequest);

        verify(recommendationValidator, times(1))
                .validateRecommendation(recommendationCaptor.capture());
        verify(recommendationValidator, times(1))
                .validateOfferedSkills(createRequest.getSkillIds());

        verify(recommendationRepository, times(1))
                .create(author.getId(), receiver.getId(), createRequest.getContent());

        verify(skillOfferRepository, times(1))
                .create(firstSkill.getId(), 1L);
        verify(skillOfferRepository, times(1))
                .create(secondSkill.getId(), 1L);

        verify(userSkillGuaranteeRepository, times(2))
                .save(guaranteeCaptor.capture());

        //verify(kafkaTemplate, times(1)).send(kafkaTopic, analyticDto);

        assertEquals(1L, createResponse.getId());
        assertEquals(1L, createResponse.getAuthorId());
        assertEquals(2L, createResponse.getReceiverId());
        assertEquals("content", createResponse.getContent());
        assertEquals(1L, createResponse.getSkillOfferIds().get(0));
        assertEquals(2L, createResponse.getSkillOfferIds().get(1));
        assertEquals(createRequest.getCreatedAt(), createResponse.getCreatedAt());
    }

    @Test
    public void update_ShouldUpdateRecommendationSuccessfully() {
        User author = new User();
        User receiver = new User();
        author.setId(1L);
        receiver.setId(2L);

        when(userRepository.getReferenceById(1L)).thenReturn(author);
        when(userRepository.getReferenceById(2L)).thenReturn(receiver);

        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        secondSkill.setId(2L);

        UpdateRecommendationRequest updateRequest = UpdateRecommendationRequest.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("updated content")
                .skillIds(List.of(1L, 2L))
                .createdAt(LocalDateTime.of(2024, 12, 20, 15, 0))
                .build();

        when(skillRepository.findAllById(updateRequest.getSkillIds())).thenReturn(List.of(firstSkill, secondSkill));

        when(skillOfferRepository.create(firstSkill.getId(), 1L)).thenReturn(3L);
        when(skillOfferRepository.create(secondSkill.getId(), 1L)).thenReturn(4L);

        SkillOffer firstSkillOffer = new SkillOffer();
        SkillOffer secondSkillOffer = new SkillOffer();
        firstSkillOffer.setId(3L);
        secondSkillOffer.setId(4L);

        when(skillOfferRepository.findById(3L)).thenReturn(Optional.of(firstSkillOffer));
        when(skillOfferRepository.findById(4L)).thenReturn(Optional.of(secondSkillOffer));

        when(userSkillGuaranteeRepository.findByUserIdAndSkillId(receiver.getId(), firstSkill.getId()))
                .thenReturn(new UserSkillGuarantee());
        when(userSkillGuaranteeRepository.findByUserIdAndSkillId(receiver.getId(), secondSkill.getId()))
                .thenReturn(new UserSkillGuarantee());

        final UpdateRecommendationResponse updateResponse = recommendationService.update(updateRequest);

        verify(recommendationValidator, times(1))
                .validateRecommendation(recommendationCaptor.capture());
        verify(recommendationValidator, times(1))
                .validateOfferedSkills(updateRequest.getSkillIds());

        verify(recommendationRepository, times(1))
                .update(author.getId(), receiver.getId(), updateRequest.getContent());

        verify(skillOfferRepository, times(1))
                .deleteAllByRecommendationId(updateRequest.getId());

        verify(skillOfferRepository, times(1))
                .create(firstSkill.getId(), updateRequest.getId());
        verify(skillOfferRepository, times(1))
                .create(secondSkill.getId(), updateRequest.getId());

        verify(userSkillGuaranteeRepository, times(1))
                .updateGuarantor(receiver.getId(), firstSkill.getId(), author.getId());
        verify(userSkillGuaranteeRepository, times(1))
                .updateGuarantor(receiver.getId(), secondSkill.getId(), author.getId());

        assertEquals(1L, updateResponse.getId());
        assertEquals(1L, updateResponse.getAuthorId());
        assertEquals(2L, updateResponse.getReceiverId());
        assertEquals("updated content", updateResponse.getContent());
        assertEquals(3L, updateResponse.getSkillOfferIds().get(0));
        assertEquals(4L, updateResponse.getSkillOfferIds().get(1));
        assertEquals(updateRequest.getCreatedAt(), updateResponse.getCreatedAt());
    }
}
