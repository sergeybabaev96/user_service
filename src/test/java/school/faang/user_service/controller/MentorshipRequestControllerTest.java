package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.MentorshipAlreadyExistsException;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {
    private static final int DESCRIPTION_MAX_LENGTH = 4096;
    private static final int DESCRIPTION_MIN_LENGTH = 10;

    @InjectMocks
    private MentorshipRequestController controller;
    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Test
    public void testNullDescriptionIsInvalid() {
        testDescriptionsWithInvalidValue(null);
    }

    @Test
    public void testEmptyDescriptionIsInvalid() {
        testDescriptionsWithInvalidValue(" ");
    }

    @Test
    public void testShortestDescriptionIsInvalid() {
        testDescriptionsWithInvalidValue("q".repeat(DESCRIPTION_MIN_LENGTH - 1));
    }


    @Test
    public void testLongestDescriptionIsInvalid() {
        testDescriptionsWithInvalidValue("q".repeat(DESCRIPTION_MAX_LENGTH + 1));
    }

    private void testDescriptionsWithInvalidValue(String description) {
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .description(description)
                .build();
        ResponseEntity<?> response = controller.requestMentorship(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRequestMentorshipCalled() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .description("I want to become a very good and sought-after java developer.".repeat(3))
                .requesterId(1L)
                .receiverId(2L)
                .build();
        controller.requestMentorship(dto);
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).requestMentorship(dto);
    }

    @Test
    public void testGetRequestsCalled() {
        RequestFilterDto filterDto = RequestFilterDto.builder()
                .description("")
                .requesterId(1L)
                .receiverId(1L)
                .status(RequestStatus.ACCEPTED).build();
        ResponseEntity<?> response = controller.getRequests(filterDto);
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).getRequests(filterDto);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetRequestsNoContent() {
        RequestFilterDto filterDto = RequestFilterDto.builder()
                .description("RandomStringDescriptions1234567890987654321!@#$%^&*()_+")
                .build();
        Mockito.when(mentorshipRequestService.getRequests(filterDto)).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = controller.getRequests(filterDto);
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).getRequests(filterDto);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetRequestsNullFilter() {
        ResponseEntity<?> response = controller.getRequests(null);
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).getRequests(null);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testAcceptedRequest() {
        ResponseEntity<?> response = controller.acceptRequest(1L);
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).acceptRequest(Mockito.anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAcceptRequestNotFound() {
        long requestId = 1L;
        String message = String.format("Mentorship request with id %d not found", requestId);
        String responseMessage = String.format("Request with ID %d not found: %s", requestId, message);
        Mockito.doThrow(new EntityNotFoundException(message))
                .when(mentorshipRequestService)
                .acceptRequest(requestId);
        ResponseEntity<?> response = controller.acceptRequest(requestId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(responseMessage, response.getBody());
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).acceptRequest(requestId);
    }

    @Test
    void testAcceptRequestConflict() {
        long requestId = 1L;
        long futureMentor = 1L;
        long requesterId = 2L;
        String message = String.format("User %d is already a mentor for user %d", futureMentor, requesterId);
        String responseMessage = String.format("Conflict while accepting request with ID %d: %s", requestId, message);
        Mockito.doThrow(new MentorshipAlreadyExistsException(message))
                .when(mentorshipRequestService)
                .acceptRequest(requestId);
        ResponseEntity<?> response = controller.acceptRequest(requestId);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(responseMessage, response.getBody());
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).acceptRequest(requestId);
    }

    @Test
    void testAcceptRequestUnexpectedError() {
        long requestId = 1L;
        Mockito.doThrow(new RuntimeException())
                .when(mentorshipRequestService)
                .acceptRequest(requestId);
        ResponseEntity<?> response = controller.acceptRequest(requestId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).acceptRequest(requestId);
    }

    @Test
    void testRejectedRequest() {
        long requestId = 1L;
        RejectionDto rejection = RejectionDto.builder()
                .receiverId(1L)
                .requesterId(2L)
                .rejectionReason("Some reason")
                .build();
        ResponseEntity<?> response = controller.rejectRequest(requestId, rejection);
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).rejectRequest(requestId, rejection);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRejectRequestError() {
        long requestId = 1L;
        RejectionDto rejection = RejectionDto.builder()
                .receiverId(1L)
                .requesterId(2L)
                .rejectionReason("Some reason")
                .build();
        String message = String.format("Mentorship request with id %d not found", requestId);
        Mockito.doThrow(new EntityNotFoundException(message))
                .when(mentorshipRequestService).rejectRequest(requestId, rejection);
        ResponseEntity<?> response = controller.rejectRequest(requestId, rejection);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(message, response.getBody());
        Mockito.verify(mentorshipRequestService, Mockito.times(1)).rejectRequest(requestId, rejection);
    }
}