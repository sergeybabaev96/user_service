package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestReceiverFilter;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestRequesterFilter;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestStatusFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.TestData.getSkills;
import static school.faang.user_service.service.TestData.getUsers;
import static school.faang.user_service.service.TestData.createFilterDto;
import static school.faang.user_service.service.TestData.createRejectDto;
import static school.faang.user_service.service.TestData.createRequest;
import static school.faang.user_service.service.TestData.createRequestRcvDto;
import static school.faang.user_service.service.TestData.createSkillRequest;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationEventPublisher publisher;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private User requester;
    private User receiver;
    Skill skill1;
    Skill skill2;
    Skill skill3;
    private RecommendationRequest recommendationRequest;
    private SkillRequest skillRequest1;
    private SkillRequest skillRequest2;
    private SkillRequest skillRequest3;
    private RecommendationRequestRcvDto recommendationRequestRcvDto;
    private RejectionDto rejectionDto;

    @BeforeEach
    void setUp() {
        List<RecommendationRequestFilter> filters = new ArrayList<>(List.of(
                new RecommendationRequestStatusFilter(),
                new RecommendationRequestRequesterFilter(),
                new RecommendationRequestReceiverFilter()));

        recommendationRequestService = new RecommendationRequestServiceImpl(
                recommendationRequestRepository,
                recommendationRequestMapper,
                userRepository,
                skillRepository,
                skillRequestRepository,
                filters,
                publisher);

        List<User> users = getUsers();
        requester = users.get(0);
        receiver = users.get(1);

        recommendationRequest = createRequest(1L, requester, receiver, RequestStatus.PENDING);

        Map<String, Skill> skills = getSkills();
        skill1 = skills.get("Skill 1");
        skill2 = skills.get("Skill 2");
        skill3 = skills.get("Skill 3");
        skillRequest1 = createSkillRequest(1L, recommendationRequest, skill1);
        skillRequest2 = createSkillRequest(2L, recommendationRequest, skill2);
        skillRequest3 = createSkillRequest(3L, recommendationRequest, skill3);

        recommendationRequestRcvDto = createRequestRcvDto(requester, receiver, recommendationRequest,
                Arrays.asList(skill1.getId(), skill2.getId(), skill3.getId()));

        rejectionDto = createRejectDto("Can't confirm.");
    }

    @Test
    void testCreateRecommendationRequest_Successfully() {
        when(recommendationRequestRepository.findLatestPendingRequest(requester.getId(),
                receiver.getId())).thenReturn(Optional.empty());
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setId(1L);
                    return savedRequest;
                });
        when(skillRequestRepository.create(1L, skill1.getId())).thenReturn(skillRequest1);
        when(skillRequestRepository.create(1L, skill2.getId())).thenReturn(skillRequest2);
        when(skillRequestRepository.create(1L, skill3.getId())).thenReturn(skillRequest3);

        RecommendationRequestDto requestFromDb;
        requestFromDb = recommendationRequestService.createRequest(recommendationRequestRcvDto);

        verifyNoMoreInteractions(userRepository, recommendationRequestRepository, skillRepository);
        verify(recommendationRequestRepository, Mockito.times(1))
                .save(recommendationRequestCaptor.capture());
        assertEquals(recommendationRequestRcvDto.message(), recommendationRequestCaptor.getValue().getMessage());

        assertNotNull(requestFromDb);
        assertEquals(1L, requestFromDb.id());
        assertEquals(recommendationRequestRcvDto.requesterId(), requestFromDb.requesterId());
        assertEquals(recommendationRequestRcvDto.receiverId(), requestFromDb.receiverId());
        assertEquals(recommendationRequestRcvDto.message(), requestFromDb.message());
        assertEquals(RequestStatus.PENDING, requestFromDb.status());
        assertEquals(recommendationRequestRcvDto.skillIds(), requestFromDb.skillIds());
        RecommendationEvent event = new RecommendationEvent(requester.getId(), receiver.getId(), 1L);
        verify(publisher).publish(event);
    }

    @Test
    void testCreateRecommendationRequest_UserRequestHimself() {
        RecommendationRequestRcvDto requestDto = createRequestRcvDto(requester, requester, recommendationRequest,
                Arrays.asList(skill1.getId(), skill2.getId(), skill3.getId()));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.createRequest(requestDto));

        assertEquals(String.format("The user with id %d cannot send a request to himself", requester.getId()),
                exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_RequestPeriodIsNotExceeded() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(RecommendationRequest.builder()
                        .createdAt(LocalDateTime.now().minusMonths(3))
                        .build()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.createRequest(recommendationRequestRcvDto)
        );

        assertEquals("Recommendation request must be sent once in 6 months,"
                + " the previous request with id = 0 was no more than 6 months ago", exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_SkillIdNotExist() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(skill1.getId())).thenReturn(true);
        when(skillRepository.existsById(skill2.getId())).thenReturn(true);
        when(skillRepository.existsById(skill3.getId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.createRequest(recommendationRequestRcvDto)
        );

        assertEquals(String.format("Skill with id = %d not exist", skill3.getId()), exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_UserIdNotExist() {
        when(recommendationRequestRepository.findLatestPendingRequest(requester.getId(),
                receiver.getId())).thenReturn(Optional.empty());
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.createRequest(recommendationRequestRcvDto)
        );

        assertEquals(String.format("User with id %d not found", receiver.getId()), exception.getMessage());
    }

    @Test
    void testGetRequestById_Successfully() {
        Long id = recommendationRequest.getId();
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequestDto requestDto = recommendationRequestService.getRequest(id);

        assertNotNull(requestDto);
        assertEquals(recommendationRequest.getId(), requestDto.id());
        assertEquals(recommendationRequest.getRequester().getId(), requestDto.requesterId());
        assertEquals(recommendationRequest.getReceiver().getId(), requestDto.receiverId());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, times(1)).toRecommendationRequestDto(recommendationRequest);
    }

    @Test
    void testGetRequestById_NotFoundRequest() {
        Long id = 5L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.getRequest(id));

        assertEquals("Recommendation request with id 5 not found", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, never()).toRecommendationRequestDto(any());
    }

    @Test
    void testRejectRequest_Successfully() {
        Long id = recommendationRequest.getId();
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setStatus(RequestStatus.REJECTED);
                    return savedRequest;
                });

        RecommendationRequestDto requestDto = recommendationRequestService.rejectRequest(id, rejectionDto);

        assertNotNull(requestDto);
        assertEquals(id, requestDto.id());
        assertEquals(RequestStatus.REJECTED, requestDto.status());
        assertEquals(rejectionDto.reason(), requestDto.rejectionReason());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationRequestMapper, times(1)).toRecommendationRequestDto(recommendationRequest);
    }

    @Test
    void testRejectRequest_AlreadyRejectedRequest() {
        Long id = recommendationRequest.getId();
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.reason());
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals(String.format("The recommendation request id %d is already rejected",
                recommendationRequest.getId()), exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toRecommendationRequestDto(any());
    }

    @Test
    void testRejectRequest_NotFoundRequest() {
        Long id = 5L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals("Recommendation request id 5 not found", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toRecommendationRequestDto(any());
    }

    @Test
    void testGetRequestsWithFiltersByStatusAndRequesterAndReceiver_Successfully() {
        RecommendationRequest request1 = createRequest(1L, requester, receiver, RequestStatus.ACCEPTED);
        RecommendationRequest request2 = createRequest(2L, requester, receiver, RequestStatus.PENDING);
        RecommendationRequest request3 = createRequest(3L, requester, receiver, RequestStatus.REJECTED);
        RequestFilterDto requestFilterDto = createFilterDto(request2.getStatus(),
                request2.getRequester().getId(), request2.getReceiver().getId());

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2, request3));

        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(1, requests.size());
        assertEquals(request2.getId(), requests.get(0).id());
        assertEquals(request2.getStatus(), requests.get(0).status());
        assertEquals(request2.getRequester().getId(), requests.get(0).requesterId());
        assertEquals(request2.getReceiver().getId(), requests.get(0).receiverId());
    }

    @Test
    void testGetRequestsWithFiltersByUser_Successfully() {
        RecommendationRequest request1 = createRequest(1L, requester, receiver, RequestStatus.ACCEPTED);
        RecommendationRequest request2 = createRequest(2L, requester, receiver, RequestStatus.PENDING);
        RecommendationRequest request3 = createRequest(3L, receiver, requester, RequestStatus.REJECTED);
        RequestFilterDto requestFilterDto = createFilterDto(null, requester.getId(), null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2, request3));

        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(2, requests.size());
        assertEquals(request1.getId(), requests.get(0).id());
        assertEquals(request2.getId(), requests.get(1).id());
    }

    @Test
    void testGetRequestsWithFiltersByStatus_NotFoundRequest() {
        RecommendationRequest request1 = createRequest(1L, requester, receiver, RequestStatus.ACCEPTED);
        RecommendationRequest request2 = createRequest(2L, requester, receiver, RequestStatus.PENDING);
        RecommendationRequest request3 = createRequest(3L, receiver, requester, RequestStatus.PENDING);
        RequestFilterDto requestFilterDto = createFilterDto(RequestStatus.REJECTED, null, null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2, request3));
        assertEquals(List.of(), recommendationRequestService.getRequests(requestFilterDto));
    }
}


