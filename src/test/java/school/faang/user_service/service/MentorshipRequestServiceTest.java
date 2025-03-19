package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipAlreadyExistsException;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @InjectMocks
    MentorshipRequestService mentorshipRequestService;
    @Mock
    MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestFilter requestFilter;
    @Mock
    private MentorshipRequestFilter receiverFilter;
    @Mock
    private MentorshipRequestFilter descriptionFilter;
    @Mock
    private MentorshipRequestFilter statusFilter;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    private Map<MentorshipRequestFilter, Predicate<MentorshipRequest>> filterMap;

    void setUpBeforeTestFilters(boolean appliedFilter) {
        mentorshipRequestService = new MentorshipRequestService(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                userRepository,
                List.of(requestFilter, receiverFilter, descriptionFilter, statusFilter)
        );
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();
        User thirdUser = User.builder().id(3L).build();

        List<MentorshipRequest> mentorshipRequests = List.of(
                MentorshipRequest.builder()
                        .id(1L).requester(firstUser).receiver(secondUser)
                        .description("Some description").status(RequestStatus.PENDING).build(),
                MentorshipRequest.builder()
                        .id(2L).requester(firstUser).receiver(thirdUser)
                        .description("Some description too").status(RequestStatus.ACCEPTED).build(),
                MentorshipRequest.builder()
                        .id(3L).requester(secondUser).receiver(thirdUser)
                        .description("afdja;flkdsj;fal").status(RequestStatus.REJECTED).build(),
                MentorshipRequest.builder()
                        .id(4L).requester(firstUser).receiver(thirdUser)
                        .description("descriptions").status(RequestStatus.REJECTED).build()
        );

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequests);

        filterMap = Map.of(
                requestFilter, request -> request.getRequester().getId() == 1L,
                receiverFilter, request -> request.getReceiver().getId() == 3L,
                descriptionFilter, request -> request.getDescription().toLowerCase().contains("too"),
                statusFilter, request -> request.getStatus() == RequestStatus.ACCEPTED
        );

        List<MentorshipRequestFilter> filters = List.of(requestFilter, receiverFilter, descriptionFilter, statusFilter);
        for (MentorshipRequestFilter filter : filters) {
            if (appliedFilter) {
                when(filter.isApplicable(any())).thenReturn(appliedFilter);
                when(filter.filter(any(), any())).thenAnswer((Answer<Stream<MentorshipRequest>>) invocation -> {
                    Stream<MentorshipRequest> stream = invocation.getArgument(0);
                    return stream.filter(filterMap.get(filter));
                });
            }
        }

    }

    @Test
    @DisplayName("Test request mentorship with null-requester")
    public void testInvalidRequester() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder().build();

        mockRequestExistence(requestDto.getRequesterId(), false, requestDto.getReceiverId(), true);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "Requester not exists");
    }

    private void mockRequestExistence(Long requesterId, boolean requesterExist,
                                      Long receiverId, boolean receiverExist) {
        when(userRepository.existsById(requesterId)).thenReturn(requesterExist);
        when(userRepository.existsById(receiverId)).thenReturn(receiverExist);
    }

    @Test
    @DisplayName("Test request mentorship with null-receiver")
    public void testInvalidReceiver() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder().build();

        mockRequestExistence(requestDto.getRequesterId(), true, requestDto.getReceiverId(), false);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "Receiver not exists");
    }

    @Test
    @DisplayName("Test request mentorship with null-requester && null-receiver")
    public void testInvalidBothUsers() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder().build();

        mockRequestExistence(requestDto.getRequesterId(), false, requestDto.getReceiverId(), false);

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "Both users is null");
    }

    @Test
    @DisplayName("Test request mentorship with last request 2 month ago")
    public void testInvalidLastRequest() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterId(1L).receiverId(2L).description("Some description").build();
        User requester = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        MentorshipRequest lastRequest = MentorshipRequest.builder()
                .requester(requester)
                .receiver(receiver).createdAt(twoMonthsAgo).build();

        mockRequestExistence(requestDto.getRequesterId(), true, requestDto.getReceiverId(), true);
        mockLatestRequest(requestDto.getRequesterId(), requestDto.getReceiverId(), Optional.of(lastRequest));

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "The request was made in the last 3 months");
    }

    private void mockLatestRequest(Long requesterId, Long receiverId, Optional<MentorshipRequest> request) {
        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(request);
    }

    @Test
    @DisplayName("Test request mentorship with identical requester and receiver")
    public void testIdenticalRequesterReceiver() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterId(1L).receiverId(1L).description("Some description").build();

        mockRequestExistence(requestDto.getRequesterId(), true, requestDto.getReceiverId(), true);
        mockLatestRequest(requestDto.getRequesterId(), requestDto.getReceiverId(), Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "Requester user and receiver user is equal");
    }

    @Test
    @DisplayName("Test request mentorship is successful")
    public void testSuccessfulRequestMentorship() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterId(1L).receiverId(2L).description("Some description").build();

        mockRequestExistence(requestDto.getRequesterId(), true, requestDto.getReceiverId(), true);
        mockLatestRequest(requestDto.getRequesterId(), requestDto.getReceiverId(), Optional.empty());

        mentorshipRequestService.requestMentorship(requestDto);

        MentorshipRequest response = mentorshipRequestMapper.toEntity(requestDto);
        verify(mentorshipRequestRepository, Mockito.times(1)).save(response);
    }

    @Test
    @DisplayName("Test getRequest with applied all filters")
    void testReturnFilteredRequest() {
        setUpBeforeTestFilters(true);
        List<MentorshipRequestDto> filteredRequests = mentorshipRequestService.getRequests(new RequestFilterDto());

        assertEquals(1, filteredRequests.size());
    }

    @Test
    @DisplayName("Test getRequest with applied all filters and return empty")
    void testReturnEmpty() {
        setUpBeforeTestFilters(true);
        filterMap = Map.of(
                requestFilter, request -> request.getRequester().getId() == 1L,
                receiverFilter, request -> request.getReceiver().getId() == 8L,
                descriptionFilter, request -> request.getDescription().toLowerCase().contains("too"),
                statusFilter, request -> request.getStatus() == RequestStatus.ACCEPTED
        );
        List<MentorshipRequestDto> filteredRequests = mentorshipRequestService.getRequests(new RequestFilterDto());

        assertTrue(filteredRequests.isEmpty());
    }

    @Test
    @DisplayName("Test getRequest with null filters")
    void testGetRequestsWithNullFilter() {
        setUpBeforeTestFilters(false);

        List<MentorshipRequestDto> filteredRequests = mentorshipRequestService.getRequests(new RequestFilterDto());

        assertEquals(4, filteredRequests.size());
    }

    @Test
    @DisplayName("Test accept request with requestId not found")
    void testAcceptRequestWithIdNotFound() {
        long requestId = 1L;
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.acceptRequest(requestId),
                "Mentorship request with id " + requestId + " not found");
    }

    @Test
    @DisplayName("Test accept request with already mentors for requester")
    void testAcceptRequestWithAlreadyMentors() {
        long requestId = 1L;
        MentorshipRequest request = MentorshipRequest.builder()
                .id(requestId)
                .requester(User.builder().id(1L).mentors(List.of(User.builder().id(2L).build())).build())
                .receiver(User.builder().id(2L).build())
                .status(RequestStatus.PENDING)
                .build();
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(MentorshipAlreadyExistsException.class,
                () -> mentorshipRequestService.acceptRequest(requestId),
                "User 2 is already a mentor for user 1");
    }

    @Test
    @DisplayName("Test accept request successful")
    void testAcceptRequestSuccessful() {
        long requestId = 1L;
        MentorshipRequest request = MentorshipRequest.builder()
                .id(requestId)
                .requester(User.builder().id(1L).mentors(new ArrayList<>()).build())
                .receiver(User.builder().id(2L).mentees(new ArrayList<>()).build())
                .status(RequestStatus.PENDING)
                .build();
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        mentorshipRequestService.acceptRequest(requestId);

        verify(userRepository, Mockito.times(1)).save(request.getRequester());
        verify(userRepository, Mockito.times(1)).save(request.getReceiver());
        verify(mentorshipRequestRepository, Mockito.times(1)).save(request);
    }

    @Test
    @DisplayName("Test reject request when requestId not found")
    void testRejectRequestWithNotFoundById() {
        long requestId = 1L;
        RejectionDto rejectionDto = RejectionDto.builder().rejectionReason("Some reason").build();
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.rejectRequest(requestId, rejectionDto),
                "Mentorship request with id " + requestId + " not found");
    }

    @Test
    @DisplayName("Test reject request successful")
    void testRejectRequestSuccessful() {
        long requestId = 1L;
        MentorshipRequest request = MentorshipRequest.builder()
                .id(requestId)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .status(RequestStatus.PENDING)
                .build();
        RejectionDto rejectionDto = RejectionDto.builder().rejectionReason("Some reason").build();
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        mentorshipRequestService.rejectRequest(requestId, rejectionDto);

        verify(mentorshipRequestRepository, Mockito.times(1)).save(request);
    }
}