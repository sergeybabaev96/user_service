package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestMapper mapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRequestMentorship_ThrowsEntityNotFoundException_Requester() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);

        when(userRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto)
        );

        assertEquals("Пользователь с id " + requestDto.getRequesterId()
                + " не существует.", exception.getMessage());

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_ThrowsEntityNotFoundException_Receiver() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setReceiverId(2L);

        when(userRepository.existsById(2L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto)
        );

        assertEquals("Пользователь с id " + requestDto.getRequesterId()
                + " не существует.", exception.getMessage());

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }

    @Test
    public void testRequestMentorship_ValidRequest_SavesEntityAndReturnsDto() {

        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setDescription("Нужна помощь с Java");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        MentorshipRequest entity = new MentorshipRequest();
        entity.setRequester(new User());
        entity.setReceiver(new User());
        entity.setDescription("Нужна помощь с Java");

        when(mapper.toEntity(requestDto)).thenReturn(entity);

        when(mentorshipRequestRepository.save(entity)).thenReturn(entity);

        when(mapper.toDto(entity)).thenReturn(requestDto);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(requestDto);

        assertNotNull(result);
        assertEquals(requestDto, result);

        verify(mentorshipRequestRepository, times(1)).save(entity);
    }

    @Test
    public void testRequestMentorship_LastRequestWithinThreeMonths_ThrowsBusinessException() {

        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setDescription("Нужна помощь с Java");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        MentorshipRequest latestRequest = new MentorshipRequest();
        latestRequest.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(mentorshipRequestRepository.findLatestRequest(1L, 2L))
                .thenReturn(Optional.of(latestRequest));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto)
        );

        assertEquals("Нельзя отправлять запрос чаще чем раз в три месяца.", exception.getMessage());

        verify(mentorshipRequestRepository, never()).save(any(MentorshipRequest.class));
    }

    @Test
    public void testAcceptRequest_UserAlreadyMentor_ThrowsBusinessException() {

        long requestId = 1L;

        MentorshipRequest request = new MentorshipRequest();
        request.setStatus(RequestStatus.PENDING);

        User requester = new User();
        requester.setId(1L);
        requester.setMentors(new ArrayList<>());

        User receiver = new User();
        receiver.setId(2L);

        request.setRequester(requester);
        request.setReceiver(receiver);

        requester.getMentors().add(receiver);

        when(mentorshipRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> mentorshipRequestService.acceptRequest(requestId)
        );

        assertEquals("Пользователь уже является вашим ментором.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void testGetRequests_WithFilters() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setDescription("менторство");
        filterDto.setRequesterId(1L);
        filterDto.setReceiverId(2L);
        filterDto.setStatus(RequestStatus.PENDING);

        User requester1 = User.builder()
                .id(1L)
                .username("Иван Иванов")
                .email("ivan@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        User receiver1 = User.builder()
                .id(2L)
                .username("Петр Петров")
                .email("petr@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        User requester2 = User.builder()
                .id(3L)
                .username("Алексей Алексеев")
                .email("alexey@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        User receiver2 = User.builder()
                .id(4L)
                .username("Сергей Сергеев")
                .email("sergey@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        MentorshipRequest request1 = new MentorshipRequest();
        request1.setId(1L);
        request1.setDescription("Это запрос на менторство");
        request1.setRequester(requester1);
        request1.setReceiver(receiver1);
        request1.setStatus(RequestStatus.PENDING);

        MentorshipRequest request2 = new MentorshipRequest();
        request2.setId(2L);
        request2.setDescription("Другой запрос");
        request2.setRequester(requester2);
        request2.setReceiver(receiver2);
        request2.setStatus(RequestStatus.ACCEPTED);

        List<MentorshipRequest> allRequests = Arrays.asList(request1, request2);

        MentorshipRequestDto dto1 = new MentorshipRequestDto(
                request1.getId(),
                request1.getDescription(),
                request1.getRequester().getId(),
                request1.getReceiver().getId(),
                request1.getStatus(),
                null
        );

        when(mentorshipRequestRepository.findAll()).thenReturn(allRequests);
        when(mapper.toDto(request1)).thenReturn(dto1);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);

        assertEquals(1, result.size());
        assertEquals("Это запрос на менторство", result.get(0).getDescription());
        assertEquals(1L, result.get(0).getRequesterId());
        assertEquals(2L, result.get(0).getReceiverId());
        assertEquals(RequestStatus.PENDING, result.get(0).getStatus());

        verify(mentorshipRequestRepository, times(1)).findAll();
        verify(mapper, times(1)).toDto(request1);
    }

    @Test
    public void testGetRequests_NoFilters() {
        RequestFilterDto filterDto = new RequestFilterDto();

        User requester1 = User.builder()
                .id(1L)
                .username("Иван Иванов")
                .email("ivan@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        User receiver1 = User.builder()
                .id(2L)
                .username("Петр Петров")
                .email("petr@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        User requester2 = User.builder()
                .id(3L)
                .username("Алексей Алексеев")
                .email("alexey@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        User receiver2 = User.builder()
                .id(4L)
                .username("Сергей Сергеев")
                .email("sergey@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        MentorshipRequest request1 = new MentorshipRequest();
        request1.setId(1L);
        request1.setDescription("Это запрос на менторство");
        request1.setRequester(requester1);
        request1.setReceiver(receiver1);
        request1.setStatus(RequestStatus.PENDING);

        MentorshipRequest request2 = new MentorshipRequest();
        request2.setId(2L);
        request2.setDescription("Другой запрос");
        request2.setRequester(requester2);
        request2.setReceiver(receiver2);
        request2.setStatus(RequestStatus.ACCEPTED);

        List<MentorshipRequest> allRequests = Arrays.asList(request1, request2);

        MentorshipRequestDto dto1 = new MentorshipRequestDto(
                request1.getId(),
                request1.getDescription(),
                request1.getRequester().getId(),
                request1.getReceiver().getId(),
                request1.getStatus(),
                null
        );

        MentorshipRequestDto dto2 = new MentorshipRequestDto(
                request2.getId(),
                request2.getDescription(),
                request2.getRequester().getId(),
                request2.getReceiver().getId(),
                request2.getStatus(),
                null
        );

        when(mentorshipRequestRepository.findAll()).thenReturn(allRequests);
        when(mapper.toDto(request1)).thenReturn(dto1);
        when(mapper.toDto(request2)).thenReturn(dto2);

        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(filterDto);

        assertEquals(2, result.size());

        assertEquals("Это запрос на менторство", result.get(0).getDescription());
        assertEquals(1L, result.get(0).getRequesterId());
        assertEquals(2L, result.get(0).getReceiverId());
        assertEquals(RequestStatus.PENDING, result.get(0).getStatus());

        assertEquals("Другой запрос", result.get(1).getDescription());
        assertEquals(3L, result.get(1).getRequesterId());
        assertEquals(4L, result.get(1).getReceiverId());
        assertEquals(RequestStatus.ACCEPTED, result.get(1).getStatus());

        verify(mentorshipRequestRepository, times(1)).findAll();
        verify(mapper, times(1)).toDto(request1);
        verify(mapper, times(1)).toDto(request2);
    }

    @Test
    public void testAcceptRequest_Success() {
        long requestId = 1L;

        User requester = User.builder()
                .id(1L)
                .username("Иван Иванов")
                .email("ivan@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .mentors(new ArrayList<>())
                .build();

        User receiver = User.builder()
                .id(2L)
                .username("Петр Петров")
                .email("petr@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userRepository.save(requester)).thenReturn(requester);
        when(mentorshipRequestRepository.save(request)).thenReturn(request);

        mentorshipRequestService.acceptRequest(requestId);

        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
        assertTrue(requester.getMentors().contains(receiver));

        verify(mentorshipRequestRepository, times(1)).findById(requestId);
        verify(userRepository, times(1)).save(requester);
        verify(mentorshipRequestRepository, times(1)).save(request);
    }

    @Test
    public void testAcceptRequest_RequestNotFound() {
        long requestId = 1L;

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            mentorshipRequestService.acceptRequest(requestId);
        });

        assertEquals("Запрос на менторство не найден.", exception.getMessage());

        verify(mentorshipRequestRepository, times(1)).findById(requestId);
        verify(userRepository, never()).save(any());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    public void testAcceptRequest_RequestAlreadyAccepted() {
        long requestId = 1L;

        User requester = User.builder()
                .id(1L)
                .username("Иван Иванов")
                .email("ivan@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .mentors(new ArrayList<>())
                .build();

        User receiver = User.builder()
                .id(2L)
                .username("Петр Петров")
                .email("petr@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            mentorshipRequestService.acceptRequest(requestId);
        });

        assertEquals("Запрос уже принят.", exception.getMessage());

        verify(mentorshipRequestRepository, times(1)).findById(requestId);
        verify(userRepository, never()).save(any());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    public void testAcceptRequest_UserAlreadyMentor() {
        long requestId = 1L;

        User requester = User.builder()
                .id(1L)
                .username("Иван Иванов")
                .email("ivan@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .mentors(new ArrayList<>())
                .build();

        User receiver = User.builder()
                .id(2L)
                .username("Петр Петров")
                .email("petr@example.com")
                .password("password")
                .active(true)
                .country(new Country())
                .build();

        requester.getMentors().add(receiver);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            mentorshipRequestService.acceptRequest(requestId);
        });

        assertEquals("Пользователь уже является вашим ментором.", exception.getMessage());

        verify(mentorshipRequestRepository, times(1)).findById(requestId);
        verify(userRepository, never()).save(any());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    public void testRejectRequest_Success() {
        long requestId = 1L;
        RejectionDto rejection = new RejectionDto("Недостаточно опыта.");

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(mentorshipRequestRepository.save(request)).thenReturn(request);

        mentorshipRequestService.rejectRequest(requestId, rejection);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals("Недостаточно опыта.", request.getRejectionReason());

        verify(mentorshipRequestRepository, times(1)).findById(requestId);
        verify(mentorshipRequestRepository, times(1)).save(request);
    }

    @Test
    public void testRejectRequest_RequestNotFound() {
        long requestId = 1L;
        RejectionDto rejection = new RejectionDto("Недостаточно опыта.");

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mentorshipRequestService.rejectRequest(requestId, rejection);
        });

        assertEquals("Запрос на менторство не найден.", exception.getMessage());

        verify(mentorshipRequestRepository, times(1)).findById(requestId);
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    public void testRejectRequest_RequestAlreadyRejected() {
        long requestId = 1L;
        RejectionDto rejection = new RejectionDto("Недостаточно опыта.");

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setStatus(RequestStatus.REJECTED);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            mentorshipRequestService.rejectRequest(requestId, rejection);
        });

        assertEquals("Запрос уже отклонен.", exception.getMessage());

        verify(mentorshipRequestRepository, times(1)).findById(requestId);
        verify(mentorshipRequestRepository, never()).save(any());
    }
}

