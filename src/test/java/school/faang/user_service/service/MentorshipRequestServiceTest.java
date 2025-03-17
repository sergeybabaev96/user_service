package school.faang.user_service.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.constants.ErrorMessages.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentor.MentorshipRequestDto;
import school.faang.user_service.dto.mentor.RejectionDto;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.dto.mentor.RequestStatusDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentor.DescriptionFilter;
import school.faang.user_service.filter.mentor.RequestFilter;
import school.faang.user_service.filter.mentor.StatusFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.RequestFilterMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    private static final int REQUEST_COOLDOWN_MONTHS = 3;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private RequestFilter requestDescriptionFilter;
    @Mock
    private RequestFilter requestStatusFilter;


    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Spy
    private RequestFilterMapper requestFilterMapper = Mappers.getMapper(RequestFilterMapper.class);

    @Captor
    private ArgumentCaptor<MentorshipRequest> mentorshipRequestCaptor;
    @Captor
    private ArgumentCaptor<List<MentorshipRequest>> mentorshipRequestStreamCaptor;

    private User requester, receiver;
    private MentorshipRequestDto mentorshipRequestDto;
    private RequestFilterDto requestFilterDto;
    private RejectionDto rejectionDto;
    private long requestId;
    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    void setUp() {
        requester = new User();
        receiver = new User();
        requester.setId(1L);
        receiver.setId(2L);
        receiver.setMentors(new ArrayList<>());

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setDescription("Valid description for mentorship request");
        mentorshipRequestDto.setRequesterId(requester.getId());
        mentorshipRequestDto.setReceiverId(receiver.getId());

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setId(requestId);

        rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Reason reject");

        requestId = 1L;

        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setDescription("Description");
        requestFilterDto.setStatus(RequestStatusDto.ACCEPTED);

        requestDescriptionFilter = new StatusFilter();
        requestStatusFilter = new DescriptionFilter();
    }

    //Positive
    @Test
    void testRequestMentorship() {
        //Arrange
        isExistById(true);
        when(userService.findById(requester.getId())).thenReturn(requester);
        when(userService.findById(receiver.getId())).thenReturn(receiver);
        //Act
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        //Assert
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequestCaptor.capture());
        MentorshipRequest mentorshipRequest = mentorshipRequestCaptor.getValue();
        assertEquals(RequestStatus.PENDING, mentorshipRequest.getStatus());
        assertEquals(receiver, mentorshipRequest.getReceiver());
        assertEquals(requester, mentorshipRequest.getRequester());
    }

    @Test
    void testGetRequests() {
        //Arrange
        mentorshipRequestService = new MentorshipRequestService(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                userService,
                requestFilterMapper,
                List.of(requestDescriptionFilter, requestStatusFilter));
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setId(1);
        request1.setDescription("Description");
        request1.setStatus(RequestStatus.REJECTED);
        request2.setId(2);
        request2.setDescription("abc");
        request2.setStatus(RequestStatus.ACCEPTED);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        //Act
        List<RequestFilterDto> result = mentorshipRequestService.getRequests(requestFilterDto);

        //Assert
        verify(requestFilterMapper, times(1)).toListDto(mentorshipRequestStreamCaptor.capture());
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRequests2() {
        //Arrange
        mentorshipRequestService = new MentorshipRequestService(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                userService,
                requestFilterMapper,
                List.of(requestDescriptionFilter, requestStatusFilter));
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setId(1);
        request1.setDescription("Description");
        request1.setStatus(RequestStatus.ACCEPTED);
        request2.setId(2);
        request2.setDescription("abc");
        request2.setStatus(RequestStatus.REJECTED);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        //Act
        List<RequestFilterDto> result = mentorshipRequestService.getRequests(requestFilterDto);

        //Assert
        verify(requestFilterMapper, times(1)).toListDto(mentorshipRequestStreamCaptor.capture());
        assertEquals(1, result.size());
    }

    @Test
    void testAcceptRequest() {
        //Arrange
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);
        //Act
        mentorshipRequestService.acceptRequest(requestId);
        //Assert
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
        assertEquals(receiver, mentorshipRequest.getReceiver());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequestCaptor.capture());
        assertTrue(receiver.getMentors().contains(requester));
    }

    @Test
    void testRejectRequest() {
        //Arrange
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(mentorshipRequest)).thenReturn(mentorshipRequest);
        //Act
        mentorshipRequestService.rejectRequest(requestId, rejectionDto);
        //Assert
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals(rejectionDto.getRejectionReason(), mentorshipRequest.getRejectionReason());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequestCaptor.capture());
    }

    //Negative
    @Test
    void testRequestMentorshipNullDto() {
        //Act
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                mentorshipRequestService.requestMentorship(null));
        //Assert
        assertEquals(ERROR_NULL_MENTORSHIP_REQUEST_DTO, exception.getMessage());
    }

    @Test
    void testRequestMentorshipErrorShotDescription() {
        //Arrange
        mentorshipRequestDto.setDescription(" ");
        //Assert
        assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    void testRequestMentorshipErrorSelfRequest() {
        //Arrange
        mentorshipRequestDto.setRequesterId(requester.getId());
        mentorshipRequestDto.setReceiverId(requester.getId());
        when(mentorshipRequestRepository.existsById(requester.getId())).thenReturn(true);

        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        //Assert
        assertEquals(ERROR_SELF_REQUEST, exception.getMessage());
    }

    @Test
    void testRequestMentorshipNotFoundUser() {
        //Arrange
        List<Long> missingUser = Stream.of(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .filter(id -> !mentorshipRequestRepository.existsById(id))
                .toList();
        isExistById(false);

        //Act
        String missingIds = missingUser.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        //Assert
        assertEquals(getUserNotFoundError(missingIds), exception.getMessage());
    }

    @Test
    void testRequestMentorshipAfterCooldown() {
        //Arrange
        LocalDateTime oldRequestTime = LocalDateTime.now().minusMonths(1);
        MentorshipRequest oldRequest = new MentorshipRequest();
        oldRequest.setCreatedAt(oldRequestTime);

        isExistById(true);
        when(mentorshipRequestRepository.findLatestRequest(requester.getId(), receiver.getId()))
                .thenReturn(Optional.of(oldRequest));
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.requestMentorship(mentorshipRequestDto));

        //Assert
        assertEquals(getFrequentRequestError(REQUEST_COOLDOWN_MONTHS), exception.getMessage());
    }

    @Test
    void testGetRequestsNullDto() {
        //Act
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                mentorshipRequestService.getRequests(null));
        //Assert
        assertEquals(ERROR_NULL_REQUEST_DTO, exception.getMessage());
    }

    @Test
    void testAcceptRequestNoFindRequest() {
        //Arrange
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(requestId));
        //Assert
        assertEquals(getAbsentRequestError(requestId), exception.getMessage());
    }

    @Test
    void testAcceptRequestSelfRequest() {
        //Arrange
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(requester);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(requestId));
        //Assert
        assertEquals(ERROR_SELF_REQUEST, exception.getMessage());
    }

    @Test
    void testAcceptRequestIsAlreadyMentor() {
        //Arrange
        List<User> mentors = new ArrayList<>();
        mentors.add(requester);
        receiver.setMentors(mentors);
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(mentorshipRequest));
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(requestId));
        //Assert
        assertEquals(ERROR_ALREADY_MENTOR, exception.getMessage());
    }

    @Test
    void testRejectRequestNullDto() {
        //Act
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                mentorshipRequestService.rejectRequest(0, null));
        //Assert
        assertEquals(ERROR_NULL_REJECTION_DTO, exception.getMessage());
    }

    @Test
    void testRejectRequestNullReason() {
        //Arrange
        rejectionDto.setRejectionReason(null);
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.rejectRequest(requestId, rejectionDto));
        //Assert
        assertEquals(ERROR_EMPTY_REJECTION, exception.getMessage());
    }

    @Test
    void testRejectRequestIsBlankReason() {
        //Arrange
        rejectionDto.setRejectionReason("");
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.rejectRequest(requestId, rejectionDto));
        //Assert
        assertEquals(ERROR_EMPTY_REJECTION, exception.getMessage());
    }

    @Test
    void testRejectRequestNoFoundRequest() {
        //Arrange
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                mentorshipRequestService.rejectRequest(requestId, rejectionDto));
        //Assert
        assertEquals(getAbsentRequestError(requestId), exception.getMessage());
    }

    private void isExistById(boolean existById) {
        when(mentorshipRequestRepository.existsById(requester.getId())).thenReturn(existById);
        when(mentorshipRequestRepository.existsById(receiver.getId())).thenReturn(existById);
    }
}