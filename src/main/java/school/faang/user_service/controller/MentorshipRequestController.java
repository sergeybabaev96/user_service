package school.faang.user_service.controller;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorshiprequest")
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
        return service.getRequests(filter);
    }

    @PutMapping(value = "/accept/{id}")
    public void acceptRequest(@PathParam(value = "id") long id) {
        service.acceptRequest(id);
    }

    @PutMapping(value = "/reject/{id}")
    public void rejectRequest(@PathParam(value = "id") Long id, @RequestBody RejectionDto rejection) {
        service.rejectRequest(id, rejection);
    }
}
