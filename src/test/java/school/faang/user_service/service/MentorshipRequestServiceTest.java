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
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private List<MentorshipRequestFilter> filters;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Test
    @DisplayName("Test request mentorship with null-requester or null-receiver")
    public void testNullIdsInvalidRequest() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterId(1L).receiverId(2L).description("Some description").build();
        User requester = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();

        when(userRepository.existsById(requestDto.getRequesterId())).thenReturn(false);
        when(userRepository.existsById(requestDto.getReceiverId())).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "Requester not exists");
        when(userRepository.existsById(requestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(requestDto.getReceiverId())).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "Receiver not exists");
        MentorshipRequestDto nullBothUsersDto = MentorshipRequestDto.builder()
                .description("Some descriptions").build();
        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(nullBothUsersDto),
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

        when(userRepository.existsById(requestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(requestDto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(
                requestDto.getRequesterId(),
                requestDto.getReceiverId())
        ).thenReturn(Optional.of(lastRequest));

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "The request was made in the last 3 months");
    }

    @Test
    @DisplayName("Test request mentorship with identical requester and receiver")
    public void testIdenticalRequesterReceiver() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterId(1L).receiverId(1L).description("Some description").build();

        when(userRepository.existsById(requestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(requestDto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(
                requestDto.getRequesterId(),
                requestDto.getReceiverId())
        ).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto),
                "Requester user and receiver user is equal");
    }

    @Test
    @DisplayName("Test request mentorship is successful")
    public void testSuccessfulRequestMentorship() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterId(1L).receiverId(2L).description("Some description").build();
        User requester = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();

        when(userRepository.existsById(requestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(requestDto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(
                requestDto.getRequesterId(),
                requestDto.getReceiverId())
        ).thenReturn(Optional.empty());

        mentorshipRequestService.requestMentorship(requestDto);

        MentorshipRequest response = mentorshipRequestMapper.toEntity(requestDto);
        verify(mentorshipRequestRepository, Mockito.times(1)).save(response);
    }

    @Test
    void testReturnFilteredRequest() {
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();
        User thirdUser = User.builder().id(3L).build();
        MentorshipRequest firstRequest = MentorshipRequest.builder()
                .id(1L)
                .requester(firstUser)
                .receiver(secondUser)
                .description("Some description").build();
        MentorshipRequest secondRequest = MentorshipRequest.builder()
                .id(2L)
                .requester(firstUser)
                .receiver(thirdUser)
                .description("Some description too").build();
        MentorshipRequest thirdRequest = MentorshipRequest.builder()
                .id(3L)
                .requester(secondUser)
                .receiver(thirdUser)
                .description("afdja;flkdsj;fal").build();
        List<MentorshipRequest> requests = List.of(firstRequest, secondRequest, thirdRequest);
        RequestFilterDto filterDto = RequestFilterDto.builder()
                .requesterId(1L).build();
        when(mentorshipRequestRepository.findAll()).thenReturn(requests);

        List<MentorshipRequestDto> filteredRequests = mentorshipRequestService.getRequests(filterDto);

        assertEquals(2, filteredRequests.size());
    }

    @Test
    void testGetRequestsWithNullFilter() {
    }

    @Test
    void acceptRequest() {
    }

    @Test
    void rejectRequest() {
    }
}