package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    public static final int RECOMMENDATION_REQUEST_MIN_DISTANCE_MONTHS = 1;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserService userService;
    @Mock
    private SkillRequestService skillRequestService;
    @Mock
    private SkillService skillService;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = Mappers.getMapper(
            RecommendationRequestMapper.class);
    @Spy
    private SkillRequestMapper skillRequestMapper = Mappers.getMapper(SkillRequestMapper.class);

    @Mock
    private List<RecommendationRequestFilter> filters;

    @Captor
    private ArgumentCaptor<RecommendationRequestDto> recommendationRequestDtoCaptor;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                recommendationRequestService,
                "recommendationRequestMinDistanceMonths",
                RECOMMENDATION_REQUEST_MIN_DISTANCE_MONTHS);
    }

    @Test
    public void testCreate_failed_on_request_earlier_required_months() {
        var recommendationRequest = createRecommendationRequestDto();
        var latestRequest = RecommendationRequest.builder()
                .createdAt(LocalDateTime.now())
                .build();
        setLatestRequest(recommendationRequest, Optional.of(latestRequest));

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_failed_on_null_skills() {
        var recommendationRequest = createRecommendationRequestDto();
        setLatestRequest(recommendationRequest, Optional.empty());

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_failed_on_empty_skills() {
        var recommendationRequest = createRecommendationRequestDto();
        recommendationRequest.setSkills(List.of());
        setLatestRequest(recommendationRequest, Optional.empty());

        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_failed_on_has_missing_skills() {
        // Arrange
        var recommendationRequest = createRecommendationRequestDto();

        var skillId = 3L;
        var skillRequest = new SkillRequestDto(skillId);
        recommendationRequest.setSkills(List.of(skillRequest));

        setLatestRequest(recommendationRequest, Optional.empty());
        when(skillService.doesSkillExists(skillId)).thenReturn(false);

        // Act + Assert
        assertThrows(
                DataValidationException.class,
                () -> recommendationRequestService.create(recommendationRequest));
    }

    @Test
    public void testCreate_save_data_if_request_elder_required_months() {
        // Arrange
        var recommendationRequest = createRecommendationRequestDto();
        var requester = prepareRequester(recommendationRequest);
        var receiver = prepareReceiver(recommendationRequest);

        var skillId = 3L;
        var skillRequest = new SkillRequestDto(skillId);
        var skillRequests = List.of(skillRequest);
        recommendationRequest.setSkills(skillRequests);

        var latestRequest = RecommendationRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(RECOMMENDATION_REQUEST_MIN_DISTANCE_MONTHS + 1))
                .build();
        setLatestRequest(recommendationRequest, Optional.of(latestRequest));
        when(skillService.doesSkillExists(skillId)).thenReturn(true);

        var expectedRecommendationRequestEntity = RecommendationRequest.builder()
                .id(5L)
                .requester(requester)
                .receiver(receiver)
                .message(recommendationRequest.getMessage())
                .build();
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(expectedRecommendationRequestEntity);

        // Act
        var result = recommendationRequestService.create(recommendationRequest);

        // Assert
        verify(skillRequestService, times(skillRequests.size()))
                .createSkillRequest(anyLong(), anyLong());
        assertEquals(expectedRecommendationRequestEntity.getId(), result.getId());
        assertEquals(expectedRecommendationRequestEntity.getMessage(), result.getMessage());
        checkUsersEqual(expectedRecommendationRequestEntity.getRequester(), requester);
        checkUsersEqual(expectedRecommendationRequestEntity.getReceiver(), receiver);
    }

    private static void checkUsersEqual(User expectedUser, User user) {
        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getUsername(), user.getUsername());
    }

    private void setLatestRequest(
            RecommendationRequestDto recommendationRequest,
            Optional<RecommendationRequest> latestRequest) {
        when(recommendationRequestRepository.findLatestRequest(
                recommendationRequest.getRequesterId(),
                recommendationRequest.getReceiverId()))
                .thenReturn(latestRequest);
    }

    private User prepareRequester(RecommendationRequestDto recommendationRequest) {
        var requester = User.builder()
                .id(recommendationRequest.getRequesterId())
                .username("Requester")
                .build();
        when(userService.getUserById(requester.getId())).thenReturn(requester);

        return requester;
    }

    private User prepareReceiver(RecommendationRequestDto recommendationRequest) {
        var receiver = User.builder()
                .id(recommendationRequest.getReceiverId())
                .username("Receiver")
                .build();
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        return receiver;
    }

    private RecommendationRequestDto createRecommendationRequestDto() {
        return new RecommendationRequestDto("Test message", 1L, 2L);
    }
}