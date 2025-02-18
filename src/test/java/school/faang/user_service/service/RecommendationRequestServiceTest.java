package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;


@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRequestService skillRequestService;

    @Mock
    private List<Filter<RequestFilterDto, RecommendationRequest>> filters;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setMessage("Test message");
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Проверка создания recommendationRequest")
    void testCreate() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.countExisting(anyList())).thenReturn(1);
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(skillRequestService.createSkillRequests(any(RecommendationRequest.class), anyList())).thenReturn(List.of());
        when(recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);

        RecommendationRequest result = recommendationRequestService.create(1L, 2L, "Test message", List.of(1L));

        assertNotNull(result);
        assertEquals("Test message", result.getMessage());
        assertEquals(RequestStatus.PENDING, result.getStatus());
    }

    @Test
    void testGetAllRequests() {
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        when(filters.stream()).thenReturn(Stream.of());

        List<RecommendationRequest> result = recommendationRequestService.getAllRequests(new RequestFilterDto());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test message", result.get(0).getMessage());
    }

    @Test
    void testGetRequestById() {
        when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequest result = recommendationRequestService.getRequestById(1L);

        assertNotNull(result);
        assertEquals("Test message", result.getMessage());
    }

    @Test
    void testRejectRequest() {
        when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);

        RejectionDto rejectionDto = new RejectionDto("Rejection reason");
        RecommendationRequest result = recommendationRequestService.rejectRequest(1L, rejectionDto);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Rejection reason", result.getRejectionReason());
    }
}