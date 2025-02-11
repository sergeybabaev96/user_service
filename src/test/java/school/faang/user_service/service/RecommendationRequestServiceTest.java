package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.request.ReceiverIdFilter;
import school.faang.user_service.filter.request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validation.RequestValidation;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository requestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private RequestValidation requestValidation;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private ReceiverIdFilter receiverIdFilter;

    @Captor
    private ArgumentCaptor<RecommendationRequest> requestCaptor;

    private RecommendationRequestService recommendationRequestService;

    private List<RecommendationRequestFilter> recommendationRequestFilters;
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private Skill skill;
    private User requester;
    private User receiver;
    private RejectionDto rejectionDto;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    public void setUp() {
        recommendationRequestFilters = List.of(receiverIdFilter);
        recommendationRequestService = new RecommendationRequestService(
                requestRepository,
                recommendationRequestMapper,
                requestValidation,
                skillRequestRepository,
                recommendationRequestFilters
        );

        requester = User.builder().id(1L).username("Requester").build();
        receiver = User.builder().id(2L).username("Receiver").build();

        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setId(1L);
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setStatus(RequestStatus.REJECTED);
        recommendationRequestDto.setSkillsIds(Arrays.asList(1L, 2L));
        recommendationRequestDto.setRequesterId(requester.getId());
        recommendationRequestDto.setReceiverId(receiver.getId());
        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
        recommendationRequestDto.setUpdatedAt(LocalDateTime.now());

        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setReceiverId(1L);
        requestFilterDto.setRequesterId(2L);
        requestFilterDto.setStatus(RequestStatus.REJECTED);


        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .message("Test message")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        skill = Skill.builder().id(1L).title("Hello").build();

        rejectionDto = new RejectionDto();
        rejectionDto.setReason("Unknown information");
    }

    @Test
    public void testCreateRequest() {
        List<Skill> skills = Arrays.asList(
                Skill.builder().id(1L).title("Skill 1").build(),
                Skill.builder().id(2L).title("Skill 2").build()
        );

        when(requestValidation.validateRequest(recommendationRequestDto)).thenReturn(skills);
        when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(requestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);

        assertNotNull(result);
        assertEquals(recommendationRequestDto, result);

        verify(requestValidation).validateRequest(recommendationRequestDto);
        verify(recommendationRequestMapper).toEntity(recommendationRequestDto);
        verify(requestRepository).save(recommendationRequest);
        verify(recommendationRequestMapper).toDto(recommendationRequest);
    }

    @Test
    public void testCreateRequestEmptySkills() {

        when(requestValidation.validateRequest(recommendationRequestDto)).thenReturn(Collections.emptyList());
        when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(requestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);

        assertNotNull(result);
        assertEquals(recommendationRequestDto, result);

        verify(requestValidation).validateRequest(recommendationRequestDto);
        verify(recommendationRequestMapper).toEntity(recommendationRequestDto);
        verify(requestRepository).save(recommendationRequest);
        verify(recommendationRequestMapper).toDto(recommendationRequest);
        verify(skillRequestRepository, never()).save(any(SkillRequest.class));
    }

    @Test
    public void testRejectRequest() {

        when(requestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        when(requestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class))).thenReturn(recommendationRequestDto);

        recommendationRequestDto = recommendationRequestService.rejectRequest(1L, rejectionDto);

        assertNotNull(recommendationRequestDto);
        assertEquals(RequestStatus.REJECTED, recommendationRequestDto.getStatus());
        assertEquals("Unknown information", recommendationRequest.getRejectionReason());

        verify(requestRepository).findById(1L);
        verify(requestRepository).save(any(RecommendationRequest.class));
        verify(recommendationRequestMapper).toDto(any(RecommendationRequest.class));
    }

    @Test
    void testRequestRecommendation() {

        when(requestValidation.validateRequest(recommendationRequestDto)).thenReturn(Collections.singletonList(skill));
        when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(requestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class))).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.requestRecommendation(recommendationRequestDto);

        assertNotNull(result);
        assertEquals(recommendationRequestDto, result);
        assertEquals(RequestStatus.PENDING, recommendationRequestDto.getStatus());

        verify(requestValidation, times(2)).validateRequest(recommendationRequestDto);

        verify(recommendationRequestMapper).toEntity(recommendationRequestDto);
        verify(requestRepository).save(requestCaptor.capture());
        verify(recommendationRequestMapper).toDto(recommendationRequest);

        RecommendationRequest capturedRequest = requestCaptor.getValue();
        assertEquals(RequestStatus.PENDING, capturedRequest.getStatus());
        assertNotNull(capturedRequest.getCreatedAt());
        assertNotNull(capturedRequest.getUpdatedAt());
    }

    @Test
    void testGetRecommendationRequestsById() {

        when(requestRepository.findById(recommendationRequest.getId())).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.getRecommendationRequests(recommendationRequest.getId());

        assertNotNull(result);
        assertEquals(recommendationRequestDto.getId(), result.getId());
        assertEquals(recommendationRequestDto.getStatus(), result.getStatus());
        assertEquals(recommendationRequestDto.getMessage(), result.getMessage());

        verify(requestRepository).findById(recommendationRequest.getId());
        verify(recommendationRequestMapper).toDto(recommendationRequest);
    }

    @Test
    void testGetRecommendationRequests() {
        RecommendationRequest otherRequest = RecommendationRequest.builder()
                .id(2L)
                .status(RequestStatus.PENDING)
                .build();

        when(requestRepository.findAll()).thenReturn(List.of(recommendationRequest, otherRequest));

        when(receiverIdFilter.isApplicable(any())).thenReturn(true);
        when(receiverIdFilter.apply(any(), any())).thenReturn(Stream.of(recommendationRequest));

        RecommendationRequestDto rejectedRequestDto = new RecommendationRequestDto();
        rejectedRequestDto.setId(recommendationRequest.getId());
        rejectedRequestDto.setStatus(recommendationRequest.getStatus());
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(rejectedRequestDto);

        List<RecommendationRequestDto> result = recommendationRequestService.getRecommendationRequests(requestFilterDto);

        assertEquals(1, result.size());
        assertEquals(recommendationRequest.getId(), result.get(0).getId());
        assertEquals(recommendationRequest.getStatus(), result.get(0).getStatus());

        verify(requestRepository).findAll();
        verify(receiverIdFilter).apply(any(),any());
        verify(recommendationRequestMapper).toDto(recommendationRequest);
    }
}