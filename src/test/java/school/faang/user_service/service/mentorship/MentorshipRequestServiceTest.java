package school.faang.user_service.service.mentorship;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.TestData;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.AuthorFilter;
import school.faang.user_service.service.mentorship.filter.DescriptionFilter;
import school.faang.user_service.service.mentorship.filter.ReceiverFilter;
import school.faang.user_service.service.mentorship.filter.RequestFilter;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.service.mentorship.filter.StatusFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MentorshipRequestServiceTest {
    MentorshipRequestRepository mentorshipRequestRepositoryMock;
    UserRepository userRepositoryMock;
    MentorshipRequestMapper mentorshipRequestMapperSpy;
    AuthorFilter authorFilterMock;
    DescriptionFilter descriptionFilterMock;
    ReceiverFilter receiverFilterMock;
    StatusFilter statusFilterMock;
    List<RequestFilter> filters;
    MentorshipRequestService mentorshipRequestService;
    UserDto user1;
    UserDto user2;


    @BeforeEach
    void init() {
        mentorshipRequestRepositoryMock = Mockito.mock(MentorshipRequestRepository.class);
        userRepositoryMock = Mockito.mock(UserRepository.class);
        mentorshipRequestMapperSpy = Mockito.spy(MentorshipRequestMapperImpl.class);

        authorFilterMock = Mockito.spy(AuthorFilter.class);
        descriptionFilterMock = Mockito.spy(DescriptionFilter.class);
        receiverFilterMock = Mockito.spy(ReceiverFilter.class);
        statusFilterMock = Mockito.spy(StatusFilter.class);

        filters = List.of(authorFilterMock, descriptionFilterMock, receiverFilterMock, statusFilterMock);

        mentorshipRequestService =
                new MentorshipRequestServiceImpl(mentorshipRequestRepositoryMock, userRepositoryMock,
                        mentorshipRequestMapperSpy, filters);
        user1 = UserDto.builder()
                .userId(1L)
                .build();
        user2 = UserDto.builder()
                .userId(2L)
                .build();
    }

    @Test
    public void testRequestMentorshipWithoutDescriptionFailed() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requester(user1)
                .receiver(user2)
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithTheSameRequesterAndReceiverIdFailed() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requester(user1)
                .receiver(user1)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithNonExistentRequesterUserIdFailed() {
        Mockito.when(userRepositoryMock.findById(1L)).thenThrow(new IllegalArgumentException());

        var requestDto = MentorshipRequestDto.builder()
                .requester(user1)
                .receiver(user2)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithNonExistentReceiverUserIdFailed() {
        Mockito.when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(userRepositoryMock.findById(2L)).thenThrow(new IllegalArgumentException());

        var requestDto = MentorshipRequestDto.builder()
                .requester(user1)
                .receiver(user2)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithTooFrequentRequestFailed() {
        MentorshipRequest latestMentorshipRequest = new MentorshipRequest();
        latestMentorshipRequest.setId(1L);
        latestMentorshipRequest.setCreatedAt(LocalDateTime.now().minusDays(89));

        MentorshipRequestDto requestDto;
        requestDto = MentorshipRequestDto.builder()
                .requester(user1)
                .receiver(user2)
                .description("some description")
                .build();

        long requesterId = 1L;
        User requesterUser = User.builder()
                .id(requesterId)
                .build();
        long receiverId = 2L;
        User receiverUser = User.builder()
                .id(receiverId)
                .build();

        Mockito.when(mentorshipRequestRepositoryMock.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(latestMentorshipRequest));
        Mockito.when(userRepositoryMock.findById(requesterId)).thenReturn(Optional.of(requesterUser));
        Mockito.when(userRepositoryMock.findById(receiverId)).thenReturn(Optional.of(receiverUser));

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipSuccess() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requester(user1)
                .receiver(user2)
                .description("description")
                .build();

        User requesterUser = User.builder()
                .id(requesterId)
                .build();
        User receiverUser = User.builder()
                .id(receiverId)
                .build();

        Mockito.when(userRepositoryMock.findById(requesterId)).thenReturn(Optional.of(requesterUser));
        Mockito.when(userRepositoryMock.findById(receiverId)).thenReturn(Optional.of(receiverUser));

        mentorshipRequestService.requestMentorship(requestDto);
        Mockito.verify(mentorshipRequestRepositoryMock, Mockito.times(1))
                .create(requesterId, receiverId, "description");
    }

    @Test
    public void testGetRequestsSuccess() {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .authorPattern("Ali")
                .statusPattern("accept")
                .descriptionPattern("desc")
                .receiverPattern("Jack")
                .build();
        Mockito.when(mentorshipRequestRepositoryMock.findAll())
                .thenReturn(TestData.getListOfRequests());

        List<MentorshipResponseDto> requests = mentorshipRequestService.getRequests(filters);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(5L, (long) requests.get(0).id());
    }
}
