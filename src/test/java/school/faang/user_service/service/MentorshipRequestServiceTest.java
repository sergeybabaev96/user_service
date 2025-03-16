package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperDecorator;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    @InjectMocks
    private MentorshipRequestMapperDecorator mentorshipRequestMapperDecorator;


//    @Test
//    public void testInvalidLastRequest() {
//        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
//                .requesterId(1L)
//                .receiverId(2L)
//                .build();
//        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
//        MentorshipRequestDto lastRequestDto = MentorshipRequestDto.builder()
//                .requesterId(1L)
//                .receiverId(2L)
//                .createdAt(threeMonthsAgo)
//                .build();
//        User requester = User.builder().id(1L).build();
//        User receiver = User.builder().id(2L).build();
//        MentorshipRequest lastRequest = MentorshipRequest.builder()
//                .requester(requester)
//                .receiver(receiver)
//                .createdAt(threeMonthsAgo)
//                .build();
//        when(userRepository.existsById(requestDto.getRequesterId())).thenReturn(true);
//        when(userRepository.existsById(requestDto.getReceiverId())).thenReturn(true);
//        when(mentorshipRequestRepository.findLatestRequest(
//                requestDto.getRequesterId(),
//                requestDto.getReceiverId())
//        ).thenReturn(Optional.ofNullable(lastRequest));
//        assertThrows(IllegalArgumentException.class,
//                () -> mentorshipRequestService.requestMentorship(requestDto),
//                "The request was made in the last 3 months");
//    }

    @Test
    public void testNullIdsInvalidRequest() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .build();
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
    void requestMentorship() {
    }

//    @Test
//    void testGetRequestsWithNullFilter() {
//        assertEquals(Collections.emptyList(), mentorshipRequestService.getRequests(null));
//    }

    @Test
    void acceptRequest() {
    }

    @Test
    void rejectRequest() {
    }
}