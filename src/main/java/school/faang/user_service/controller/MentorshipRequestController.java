package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private static final String START_MENTORSHIP_REQUEST = "Start requestMentorship id: {}";
    private static final String END_MENTORSHIP_REQUEST = "End requestMentorship id: {}";
    private static final String START_GETS_REQUEST = "Start mentorshipRequestService.getRequests";
    private static final String END_GETS_REQUEST = "End mentorshipRequestService.getRequests";

    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        log.info(START_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        log.info(END_MENTORSHIP_REQUEST, mentorshipRequestDto.getId());
    }

    public void getRequests(MentorshipRequestDto filter) {
    public void getRequests(RequestFilterDto filter) {
        log.info(START_GETS_REQUEST);
        mentorshipRequestService.getRequests(filter);
        log.info(END_GETS_REQUEST);
    }
}
