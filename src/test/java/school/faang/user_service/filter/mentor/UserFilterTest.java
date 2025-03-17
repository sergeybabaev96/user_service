package school.faang.user_service.filter.mentor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserFilterTest {
    private final UserFilter userFilter = new UserFilter();
    private RequestFilterDto requestFilterDto;
    private User requester;
    private User receiver;
    private User requester2;
    private User receiver2;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RequestFilterDto();
        requester = new User();
        receiver = new User();
        requester.setId(1L);
        receiver.setId(2L);
        requester2 = new User();
        receiver2 = new User();
        requester2.setId(3L);
        receiver2.setId(4L);
    }

    //Positive
    @Test
    void isApplicable() {
        //Arrange
        requestFilterDto.setRequesterId(requester.getId());
        requestFilterDto.setReceiverId(receiver.getId());

        //Assert
        assertTrue(userFilter.isApplicable(requestFilterDto));
    }

    @Test
    void apply() {
        //Arrange
        requestFilterDto.setRequesterId(requester.getId());
        requestFilterDto.setReceiverId(receiver.getId());
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setRequester(requester);
        request1.setReceiver(receiver);
        request2.setRequester(requester2);
        request2.setReceiver(receiver2);
        List<MentorshipRequest> requests = List.of(request1, request2);

        //Act
        List<MentorshipRequest> result = userFilter.apply(requests.stream(), requestFilterDto).toList();

        //Assert
        assertEquals(1, result.size());
        assertEquals(requester, result.get(0).getRequester());
        assertEquals(receiver, result.get(0).getReceiver());
    }

    //Negative
    @Test
    void isApplicableNullDescription() {
        //Arrange
        requestFilterDto.setStatus(null);

        //Assert
        assertFalse(userFilter.isApplicable(requestFilterDto));
    }

    @Test
    void isApplicableNullReceiver() {
        //Arrange
        requestFilterDto.setRequesterId(requester.getId());
        requestFilterDto.setReceiverId(null);

        //Assert
        assertFalse(userFilter.isApplicable(requestFilterDto));
    }

    @Test
    void isApplicableNullRequester() {
        //Arrange
        requestFilterDto.setRequesterId(null);
        requestFilterDto.setReceiverId(receiver.getId());

        //Assert
        assertFalse(userFilter.isApplicable(requestFilterDto));
    }

    @Test
    void isApplicableNullRequesterAndReceiver() {
        //Arrange
        requestFilterDto.setRequesterId(null);
        requestFilterDto.setReceiverId(null);

        //Assert
        assertFalse(userFilter.isApplicable(requestFilterDto));
    }

    @Test
    void applyEmptyListWhenNoMatches() {
        //Arrange
        requestFilterDto.setRequesterId(requester.getId());
        requestFilterDto.setReceiverId(receiver.getId());
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setRequester(requester);
        request1.setRequester(receiver2);
        request2.setRequester(requester2);
        request2.setReceiver(receiver);
        List<MentorshipRequest> requests = List.of(request1, request2);

        //Act
        List<MentorshipRequest> result = userFilter.apply(requests.stream(), requestFilterDto).toList();

        //Assert
        assertTrue(result.isEmpty());
    }
}