package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mentor")
public class MentorshipRequestController {
    private final MentorshipRequestService service;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto request) {
        if (!request.getDescription().isEmpty()) {
            return service.requestMentorship(request);
        } else {
            throw new IllegalArgumentException("Description is empty");
        }
    }

    @GetMapping
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return service.getRequest(filter);
    }

    @PutMapping(value = "/accept/{id}")
    public void acceptRequest(@RequestBody long id) {
        service.acceptRequest(id);
    }

    @PutMapping(value = "/reject/{id}")
    public void rejectRequest(@RequestBody Long id, @RequestBody RejectionDto rejection) {
        service.rejectRequest(id, rejection);
    }
}
