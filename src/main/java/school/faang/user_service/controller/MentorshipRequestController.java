package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private static final String START_MENTORSHIP_REQUEST = "Start requestMentorship id: {}";
    private static final String END_MENTORSHIP_REQUEST = "End requestMentorship id: {}";
    private static final String START_GETS_REQUEST = "Start mentorshipRequestService.getRequests";
    private static final String END_GETS_REQUEST = "End mentorshipRequestService.getRequests";
    private static final String START_ACCEPT_REQUEST = "Start acceptRequest id: {}";
    private static final String END_ACCEPT_REQUEST = "End acceptRequest id: {}";
    private static final String START_REJECT_REQUEST = "Start rejectRequest";
    private static final String END_REJECT_REQUEST = "End rejectRequest";

    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        log.info(START_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        log.info(END_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
    }

    public List<RequestFilterDto> getRequests(RequestFilterDto filter) {
        log.info(START_GETS_REQUEST);
        List<RequestFilterDto> requestFilterDtoList = mentorshipRequestService.getRequests(filter);
        log.info(END_GETS_REQUEST);
        return requestFilterDtoList;
    }

    public void acceptRequest(long id) {
        log.info(START_ACCEPT_REQUEST, id);
        mentorshipRequestService.acceptRequest(id);
        log.info(END_ACCEPT_REQUEST, id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        log.info(START_REJECT_REQUEST);
        mentorshipRequestService.rejectRequest(id, rejection);
        log.info(END_REJECT_REQUEST);
    }
}
