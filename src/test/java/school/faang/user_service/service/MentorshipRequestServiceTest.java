package school.faang.user_service.service;

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
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
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

    void setUpBeforeTestFilters() {
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
            when(filter.isApplicable(any())).thenReturn(true);
            when(filter.filter(any(), any())).thenAnswer((Answer<Stream<MentorshipRequest>>) invocation -> {
                Stream<MentorshipRequest> stream = invocation.getArgument(0);
                return stream.filter(filterMap.get(filter));
            });
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
    void testReturnFilteredRequest() {
        setUpBeforeTestFilters();
        List<MentorshipRequestDto> filteredRequests = mentorshipRequestService.getRequests(new RequestFilterDto());

        assertEquals(1, filteredRequests.size());
    }

    @Test
    void testReturnEmpty() {
        setUpBeforeTestFilters();
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
    void testGetRequestsWithNullFilter() {
        setUpBeforeTestFilters();
        filterMap = Map.of(
                requestFilter, request -> true,
                receiverFilter, request -> true,
                descriptionFilter, request -> true,
                statusFilter, request -> true
        );
        List<MentorshipRequestDto> filteredRequests = mentorshipRequestService.getRequests(new RequestFilterDto());

        assertEquals(4, filteredRequests.size());
    }

    @Test
    void acceptRequest() {
    }

    @Test
    void rejectRequest() {
    }
}