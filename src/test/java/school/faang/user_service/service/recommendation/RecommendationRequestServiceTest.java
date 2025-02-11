package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private RecommendationRequest recommendationRequest;
    private RecommendationRequest existingRequest;
    private User requester;
    private User receiver;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(1L);

        receiver = new User();
        receiver.setId(2L);

        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .message("Please endorse my Java skills")
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        existingRequest = RecommendationRequest.builder()
                .id(2L)
                .message("Old request")
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusMonths(3))
                .build();
    }

    @Test
    void create_shouldSaveRecommendationRequest() {
        lenient().when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(recommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RecommendationRequest result = recommendationRequestService.create(recommendationRequest);

        assertNotNull(result);
        verify(recommendationRequestRepository).save(any());
        verify(skillRequestRepository).saveAll(anyList());
    }


    @Test
    void create_shouldThrowExceptionWhenRequestIsNull() {
        assertThrows(NullPointerException.class, () -> recommendationRequestService.create(null));
    }

    @Test
    void getRequests_shouldFilterRequests() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(1L);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest, existingRequest));

        List<RecommendationRequest> filteredRequests = recommendationRequestService.getRequests(filter);

        assertEquals(2, filteredRequests.size());
        assertTrue(filteredRequests.stream().allMatch(req -> req.getRequester().getId().equals(1L)));
    }

    @Test
    void getRequest_shouldReturnRequest() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequest result = recommendationRequestService.getRequest(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getRequest_shouldThrowExceptionWhenNotFound() {
        when(recommendationRequestRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.getRequest(3L));

        assertEquals("Recommendation request not found", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldRejectPendingRequest() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any())).thenReturn(recommendationRequest);

        RecommendationRequest result = recommendationRequestService.rejectRequest(1L, "Not needed");

        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Not needed", result.getRejectionReason());
    }

    @Test
    void rejectRequest_shouldThrowExceptionIfAlreadyRejectedOrAccepted() {
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.rejectRequest(1L, "Not needed"));

        assertEquals("Cannot reject a request with status ACCEPTED", exception.getMessage());

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));

        exception = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.rejectRequest(1L, "Not needed"));

        assertNotNull(exception);
        assertEquals("Cannot reject a request with status REJECTED", exception.getMessage());

    }
}
