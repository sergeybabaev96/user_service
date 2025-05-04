package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.request.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl_;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RequestValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository requestRepository;

    @Spy
    private RecommendationRequestMapperImpl_ requestMapper;

    @Mock
    private RequestValidation requestValidation;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private List<RecommendationRequestFilter> filters;

    @InjectMocks
    private RecommendationRequestService requestService;

    private final Long REQUEST_ID = 1L;
    private final RecommendationRequestDto requestDto = buildRequestDto();
    private final RecommendationRequest requestEntity = buildRequestEntity();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testRequestRecommendation() {
        when(requestMapper.toEntity(requestDto)).thenReturn(requestEntity);
        when(requestRepository.save(requestEntity)).thenReturn(requestEntity);
        when(requestValidation.validateRequest(requestDto)).thenReturn(null)
                .thenReturn(requestEntity.getReceiver().getSkills());
        when(skillRequestRepository.create(anyLong(), anyLong())).thenReturn(null);

        RecommendationRequestDto result = requestService.requestRecommendation(requestDto);

        assertNotNull(result);
        verify(requestRepository).save(requestEntity);
    }

    @Test
    void testGetRecommendationRequests() {
        RequestFilterDto filterDto = new RequestFilterDto();
        RecommendationRequestFilter filter = mock(RecommendationRequestFilter.class);
        Iterator<RecommendationRequestFilter> iterator = mock(Iterator.class);


        when(filters.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(filter);

        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(requestRepository.findAll()).thenReturn(List.of(requestEntity));
        when(filter.apply(any(), any())).thenReturn(Stream.of(requestEntity));

        List<RecommendationRequestDto> result = requestService.getRecommendationRequests(filterDto);

        assertEquals(1, result.size());
    }

    @Test
    void testGetRecommendationRequest() {
        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(requestEntity));
        when(requestMapper.toDto(requestEntity)).thenReturn(requestDto);

        RecommendationRequestDto result = requestService.getRecommendationRequest(REQUEST_ID);

        assertNotNull(result);
        assertEquals(RequestStatus.PENDING, result.getStatus());
    }

    @Test
    void testGetRecommendationRequestThrowsException() {
        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> requestService.getRecommendationRequest(REQUEST_ID));
    }

    @Test
    void testRejectRequest() {

        requestEntity.setStatus(RequestStatus.PENDING);
        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(requestEntity));
        when(requestRepository.save(requestEntity)).thenReturn(requestEntity);

        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("reason");

        RecommendationRequestDto result = requestService.rejectRequest(REQUEST_ID, rejectionDto);

        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertNotNull(requestEntity.getRejectionReason());
        verify(requestMapper).toDto(requestEntity);
    }

    @Test
    void testRejectRequestThrowsException() {
        requestEntity.setStatus(RequestStatus.ACCEPTED);
        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(requestEntity));


        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("reason");

        assertThrows(IllegalStateException.class,
                () -> requestService.rejectRequest(REQUEST_ID, rejectionDto));
    }

    private RecommendationRequestDto buildRequestDto() {
         RecommendationRequestDto requestDto1 = new RecommendationRequestDto();
        requestDto1.setId(REQUEST_ID);
        requestDto1.setRequesterId(100L);
        requestDto1.setReceiverId(200L);
        requestDto1.setMessage("Test request");
        requestDto1.setStatus(RequestStatus.PENDING);
        requestDto1.setSkillsIds(List.of(10L, 20L));
        return requestDto1;
    }

    private RecommendationRequest buildRequestEntity() {

        return RecommendationRequest.builder()
                .id(REQUEST_ID)
                .requester(buildUserEntity(100L))
                .receiver(buildUserEntity(200L))
                .message("Test request")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User buildUserEntity(Long userId) {
        return User.builder()
                .id(userId)
                .username("john_doe")
                .email("john.doe@example.com")
                .password("hashedPassword123")
                .active(true)
                .country(Country.builder().id(1L).title("Russia").build())
                .phone("123-456-7890")
                .aboutMe("Backend developer with 5+ years experience")
                .city("Moscow")
                .experience(5)
                .userProfilePic(null)
                .skills(List.of(
                        Skill.builder().id(1L).title("Java").build(),
                        Skill.builder().id(2L).title("Spring").build()
                ))
                .followers(new ArrayList<>())
                .followees(new ArrayList<>())
                .build();
    }
}