package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRequestMentorship() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setDescription("Мне нужна помощь с java");

        MentorshipRequestDto responseDto = new MentorshipRequestDto();
        responseDto.setRequesterId(1L);
        responseDto.setReceiverId(2L);
        responseDto.setDescription("Мне нужна помощь с java");
        responseDto.setStatus(RequestStatus.valueOf("PENDING"));

        when(mentorshipRequestService.requestMentorship(requestDto)).thenReturn(responseDto);
        MentorshipRequestDto result = mentorshipRequestController.requestMentorship(requestDto);

        assertEquals(1L, result.getRequesterId());
        assertEquals(2L, result.getReceiverId());
        assertEquals("Мне нужна помощь с java", result.getDescription());

        verify(mentorshipRequestService, times(1)).requestMentorship(requestDto);
    }

    @Test
    public void testGetRequests() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setDescription("Мне нужна помощь с java");
        filterDto.setRequesterId(1L);
        filterDto.setReceiverId(2L);
        filterDto.setStatus(RequestStatus.PENDING);

        MentorshipRequestDto request1 = new MentorshipRequestDto();
        request1.setId(1L);
        request1.setDescription("Мне нужна помощь с java");
        request1.setRequesterId(1L);
        request1.setReceiverId(2L);
        request1.setStatus(RequestStatus.PENDING);

        MentorshipRequestDto request2 = new MentorshipRequestDto();
        request2.setId(2L);
        request2.setDescription("Мне нужна помощь с java");
        request2.setRequesterId(1L);
        request2.setReceiverId(2L);
        request2.setStatus(RequestStatus.PENDING);

        List<MentorshipRequestDto> expectedInformation = Arrays.asList(request1, request2);
        when(mentorshipRequestService.getRequests(filterDto)).thenReturn(expectedInformation);
        List<MentorshipRequestDto> result = mentorshipRequestController.getRequests(filterDto);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getRequesterId());
        assertEquals(2L, result.get(1).getReceiverId());
        assertEquals("Мне нужна помощь с java", result.get(0).getDescription());
        assertEquals(RequestStatus.PENDING, result.get(1).getStatus());

        verify(mentorshipRequestService, times(1)).getRequests(filterDto);

    }

    @Test
    public void testAcceptRequest() {
        long requestId = 1L;
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setId(1L);
        requestDto.setRequesterId(2L);
        requestDto.setReceiverId(3L);
        requestDto.setDescription("Мне нужна помощь с java");

        when(mentorshipRequestService.acceptRequest(requestId)).thenReturn(requestDto);
        MentorshipRequestDto result = mentorshipRequestController.acceptRequest(requestId);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getRequesterId());
        assertEquals(3L, result.getReceiverId());
        assertEquals("Мне нужна помощь с java", result.getDescription());

        verify(mentorshipRequestService, times(1)).acceptRequest(requestId);
    }

    @Test
    void rejectRequest_ValidIdAndReason_ReturnsOk() {
        long requestId = 1L;
        RejectionDto requestDto = new RejectionDto();
        requestDto.setRejectionReason("Не хватает опыта.");

        MentorshipRequestDto responseDto = new MentorshipRequestDto();
        responseDto.setId(1L);
        responseDto.setRejectionReason("Не хватает опыта.");

        when(mentorshipRequestService.rejectRequest(requestId, requestDto)).thenReturn(responseDto);
        MentorshipRequestDto result = mentorshipRequestController.rejectRequest(requestId, requestDto);
        assertEquals(1L, result.getId());
        assertEquals("Не хватает опыта.", result.getRejectionReason());
    }
}
