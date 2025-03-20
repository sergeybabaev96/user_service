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
        requestFilterDto.setStatus(RequestStatusDto.ACCEPTED);

        assertTrue(statusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void apply() {
        requestFilterDto.setStatus(RequestStatusDto.REJECTED);
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setStatus(RequestStatus.REJECTED);
        request2.setStatus(RequestStatus.ACCEPTED);
        List<MentorshipRequest> requests = List.of(request1, request2);

        List<MentorshipRequest> result = statusFilter.apply(requests.stream(), requestFilterDto).toList();

        assertEquals(1, result.size());
        assertEquals(RequestStatus.REJECTED, result.get(0).getStatus());
    }

    //Negative
    @Test
    void isApplicableNullDescription() {
        requestFilterDto.setStatus(null);

        assertFalse(statusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void applyEmptyListWhenNoMatches() {
        requestFilterDto.setStatus(RequestStatusDto.REJECTED);
        MentorshipRequest request1 = new MentorshipRequest();
        request1.setStatus(RequestStatus.ACCEPTED);
        List<MentorshipRequest> requests = List.of(request1);

        List<MentorshipRequest> result = statusFilter.apply(requests.stream(), requestFilterDto).toList();

        assertTrue(result.isEmpty());
    }
}