package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipRequestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {
    private static final long REQUEST_ID = 1L;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    private MentorshipRequestDto getRequestDto() {
        return new MentorshipRequestDto(1L, "description", 1L, 2L, RequestStatus.PENDING, null, null);
    }

    @Test
    void testRequestMentorship() {
        MentorshipRequestDto requestDto = getRequestDto();
        when(mentorshipRequestService.requestMentorship(any())).thenReturn(requestDto);

        MentorshipRequestDto response = mentorshipRequestController.requestMentorship(requestDto);

        verify(mentorshipRequestService, times(1)).requestMentorship(any());
        assertNotNull(response);
    }

    @Test
    void positiveGetRequests() {
        MentorshipRequestDto requestDto = getRequestDto();
        RequestFilterDto filter = new RequestFilterDto("status", 1L, 2L, RequestStatus.PENDING);
        when(mentorshipRequestService.getRequests(any())).thenReturn(Collections.singletonList(requestDto));

        List<MentorshipRequestDto> response = mentorshipRequestController.getRequests(filter);

        verify(mentorshipRequestService, times(1)).getRequests(any());
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void positiveAcceptRequest() {
        MentorshipRequestDto requestDto = getRequestDto();
        when(mentorshipRequestService.acceptRequest(anyLong())).thenReturn(requestDto);

        MentorshipRequestDto response = mentorshipRequestController.acceptRequest(REQUEST_ID);

        verify(mentorshipRequestService, times(1)).acceptRequest(anyLong());
        assertNotNull(response);
    }

    @Test
    void positiveRejectRequest() {
        MentorshipRequestDto requestDto = getRequestDto();
        RejectionDto rejectionDto = new RejectionDto("reason");
        when(mentorshipRequestService.rejectRequest(anyLong(), any())).thenReturn(requestDto);

        MentorshipRequestDto response = mentorshipRequestController.rejectRequest(1L, rejectionDto);

        verify(mentorshipRequestService, times(1)).rejectRequest(anyLong(), any());
        assertNotNull(response);
    }
}
