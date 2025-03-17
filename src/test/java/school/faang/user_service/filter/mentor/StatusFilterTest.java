package school.faang.user_service.filter.mentor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.dto.mentor.RequestStatusDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest {
    private final StatusFilter statusFilter = new StatusFilter();
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RequestFilterDto();
    }

    //Positive
    @Test
    void isApplicable() {
        //Arrange
        requestFilterDto.setStatus(RequestStatusDto.ACCEPTED);

        //Assert
        assertTrue(statusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void apply() {
        //Arrange
        requestFilterDto.setStatus(RequestStatusDto.REJECTED);
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setStatus(RequestStatus.REJECTED);
        request2.setStatus(RequestStatus.ACCEPTED);
        List<MentorshipRequest> requests = List.of(request1, request2);

        //Act
        List<MentorshipRequest> result = statusFilter.apply(requests.stream(), requestFilterDto).toList();

        //Assert
        assertEquals(1, result.size());
        assertEquals(RequestStatus.REJECTED, result.get(0).getStatus());
    }

    //Negative
    @Test
    void isApplicableNullDescription() {
        //Arrange
        requestFilterDto.setStatus(null);

        //Assert
        assertFalse(statusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void applyEmptyListWhenNoMatches() {
        //Arrange
        requestFilterDto.setStatus(RequestStatusDto.REJECTED);
        MentorshipRequest request1 = new MentorshipRequest();
        request1.setStatus(RequestStatus.ACCEPTED);
        List<MentorshipRequest> requests = List.of(request1);

        //Act
        List<MentorshipRequest> result = statusFilter.apply(requests.stream(), requestFilterDto).toList();

        //Assert
        assertTrue(result.isEmpty());
    }
}