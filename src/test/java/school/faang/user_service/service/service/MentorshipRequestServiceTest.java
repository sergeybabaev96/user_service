package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.filter.TestMentorshipRequestDescriptionFilter;
import school.faang.user_service.service.filter.TestMentorshipRequestReceiverIdFilter;
import school.faang.user_service.service.filter.TestMentorshipRequestRequesterIdFilter;
import school.faang.user_service.service.filter.TestMentorshipRequestStatusFilter;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    private final MentorshipRequestFilter mentorshipRequestDescriptionFilter = new TestMentorshipRequestDescriptionFilter();
    private final MentorshipRequestFilter mentorshipRequestReceiverIdFilter = new TestMentorshipRequestReceiverIdFilter();
    private final MentorshipRequestFilter mentorshipRequestRequesterIdFilter = new TestMentorshipRequestRequesterIdFilter();
    private final MentorshipRequestFilter mentorshipRequestStatusFilter = new TestMentorshipRequestStatusFilter();

    private MentorshipRequestService service;

    private RequestFilterDto requestFilterDto;
    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequest mentorshipRequest;
    private User requester;
    private User receiver;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(REQUESTER_ID);

        receiver = new User();
        receiver.setId(RECEIVER_ID);

        requestFilterDto = new RequestFilterDto("description", REQUESTER_ID, RECEIVER_ID, RequestStatus.PENDING);
        mentorshipRequestDto = new MentorshipRequestDto(1L, "description", REQUESTER_ID, RECEIVER_ID, RequestStatus.PENDING, null, null);

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setDescription("description");
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        service = new MentorshipRequestService(mentorshipRequestRepository, userRepository, mentorshipRequestMapper,
                List.of(mentorshipRequestDescriptionFilter, mentorshipRequestReceiverIdFilter, mentorshipRequestRequesterIdFilter, mentorshipRequestStatusFilter));
    }

    @Test
    void testGetRequests() {
        // Arrange
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(mentorshipRequest));

        // Act
        List<MentorshipRequestDto> result = service.getRequests(requestFilterDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("description", result.get(0).description());
        assertEquals(1L, result.get(0).requesterId());
        assertEquals(2L, result.get(0).receiverId());
        assertEquals(RequestStatus.PENDING, result.get(0).status());
    }

    @Test
    void testRequestMentorshipSuccessfully() {
        // Arrange
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(mentorshipRequestRepository.findLatestRequest(requester.getId(), receiver.getId())).thenReturn(Optional.empty());
        when(mentorshipRequestRepository.create(requester.getId(), receiver.getId(), mentorshipRequestDto.description()))
                .thenReturn(mentorshipRequest);
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        // Act
        MentorshipRequestDto result = service.requestMentorship(mentorshipRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(mentorshipRequestRepository, times(1)).create(requester.getId(), receiver.getId(), mentorshipRequestDto.description());
    }

    @Test
    void testThrowExceptionIfRequestAlreadyAccepted() {
        // Arrange
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.acceptRequest(1L));
        assertEquals("Request #1 already accepted or rejected", exception.getMessage());
    }

    @Test
    void testRejectRequestSuccessfully() {
        // Arrange
        RejectionDto rejectionDto = new RejectionDto("Not interested");
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        // Act
        MentorshipRequestDto result = service.rejectRequest(1L, rejectionDto);

        // Assert
        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("Not interested", mentorshipRequest.getRejectionReason());
        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
    }

    @Test
    void testThrowExceptionIfRequestAlreadyRejected() {
        // Arrange
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.rejectRequest(1L, new RejectionDto("Not interested")));
        assertEquals("Request #1  already accepted or rejected", exception.getMessage());
    }

    @Test
    void testThrowExceptionIfRequesterNotFound() {
        // Arrange
        when(userRepository.findById(requester.getId())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.requestMentorship(mentorshipRequestDto));
        assertEquals("Requester for request #1  not found", exception.getMessage());
    }

    @Test
    void testThrowExceptionIfReceiverNotFound() {
        // Arrange
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.requestMentorship(mentorshipRequestDto));
        assertEquals("Receiver not found", exception.getMessage());
    }

    @Test
    void testThrowExceptionIfRequestAlreadyAcceptedOrRejected() {
        // Arrange
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.acceptRequest(1L));
        assertEquals("Request #1 already accepted or rejected", exception.getMessage());

        // Act & Assert
        exception = assertThrows(IllegalArgumentException.class, () -> {
            RejectionDto rejectionDto = new RejectionDto("Not interested");
            service.rejectRequest(1L, rejectionDto);
        });
        assertEquals("Request #1  already accepted or rejected", exception.getMessage());
    }
}
