package school.faang.user_service.controller.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.BaseTest;
import school.faang.user_service.data.RecommendationRequestData;
import school.faang.user_service.data.SkillData;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest extends BaseTest {
    @Autowired
    private RecommendationRequestController recommendationRequestController;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SkillRepository skillRepository;
    @MockBean
    private RecommendationRequestRepository recommendationRequestRepository;
    @MockBean
    private SkillRequestRepository skillRequestRepository;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userRepository);
        Mockito.reset(skillRepository);
        Mockito.reset(recommendationRequestRepository);
        Mockito.reset(skillRequestRepository);
    }

    @Test
    public void requestRecommendationSuccess() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        mockData(data);

        RecommendationRequestDto response = recommendationRequestController.requestRecommendation(data.toDto());
        assertNotNull(response);

        assertEquals(data.getMessage(), response.getMessage());
    }

    @Test
    public void requestRecommendationLessMinMonthFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;
        mockData(data);

        recommendationRequestController.requestRecommendation(data.toDto());

        try {
            mockData(data);
            mockFindLatestPendingRequest(true);
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (IllegalArgumentException e) {
            assertEquals("Less than min months have passed since the previous request", e.getMessage());
        }
    }

    @Test
    public void requestRecommendationNotFoundUserFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        when(userRepository.existsById(any())).thenReturn(false);
        try {
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (EntityNotFoundException e) {
            assertEquals("User with id 1 not found", e.getMessage());
        }
    }

    @Test
    public void requestRecommendationNullMessageFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA_NULL_MESSAGE;

        try {
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (IllegalArgumentException e) {
            assertEquals("Message must not be null", e.getMessage());
        }
    }
    @Disabled
    @Test
    void getRecommendationRequests() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(data.toRecommendationRequest()));
        List<RecommendationRequestDto> response = recommendationRequestController.getRecommendationRequests(data.toFilterDto());
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Disabled
    @Test
    void getRecommendationRequest() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        when(recommendationRequestRepository.findById(any())).thenReturn(Optional.of(data.toRecommendationRequest()));
        RecommendationRequestDto request = recommendationRequestController.getRecommendationRequest(data.getId());
        assertNotNull(request);
        assertEquals(data.getId(), request.getId());
    }

    private void mockData(RecommendationRequestData data) {
        mockUserData(data.getRequester().getUser());
        mockFindLatestPendingRequest(false);
        mockSkillsFindById(data);
        when(recommendationRequestRepository.save(any())).thenReturn(data.toRecommendationRequest());
        when(skillRequestRepository.saveAll(any())).thenReturn(data.getSkillsRequested().stream().map(skillData ->
                SkillRequest.builder()
                        .skill(skillData.toSkill())
                        .request(data.toRecommendationRequest())
                        .id(1)
                        .build()
                ).toList()
        );
    }

    private void mockSkillsFindById(RecommendationRequestData data) {
        when(skillRepository.existsById(any())).thenReturn(true);
        when(skillRepository.findAllById(any())).thenReturn(data.getSkillsRequested().stream().map(SkillData::toSkill).toList());
        when(skillRepository.findById(any())).thenReturn(Optional.of(data.getSkillsRequested().get(0).toSkill()));
    }

    private void mockFindLatestPendingRequest(Boolean returnValue) {
        when(recommendationRequestRepository.isLatestPendingRequestCreatedAfterThenExists(anyLong(), any())).thenReturn(returnValue);
    }

    private void mockUserData(User user) {
        when(userRepository.existsById(any())).thenReturn(true);
        when(userRepository.findAllById(any())).thenReturn(List.of(user));
    }
}