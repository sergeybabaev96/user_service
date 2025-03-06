package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    private static final int DESCRIPTION_MAX_LENGTH = 4096;
    private static final int DESCRIPTION_MIN_LENGTH = 100;
    private static final int REJECTION_REASON_MAX_LENGTH = 4096;
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequest(mentorshipRequestDto);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long mentorshipRequestId) {
        mentorshipRequestService.acceptRequest(mentorshipRequestId);
    }

        /*
        В методе нужно найти нужный запрос в базе, если его нет,
        выкинуть исключение. Сменить статус запроса на REJECTED и
        указать причину отклонения. Для этого использовать поле reason из RejectionDto.
        */
    public void rejectRequest(long mentorshipRequestId, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(mentorshipRequestId, rejection);

    }




    /**************************************************************************************/
    private void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        String desc = mentorshipRequestDto.getDescription();
        if (desc.isBlank()) {
            throw new IllegalArgumentException("Description is empty");
        }
        if (desc.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("Description has more than 4096 characters");
        }
        if (desc.length() < DESCRIPTION_MIN_LENGTH) {
            throw new IllegalArgumentException("Tell more about your reasons for the request");
        }
    }
}
