package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user_service/mentorship")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("requests")
    public ResponseEntity<MentorshipResponseDto> requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        MentorshipResponseDto response = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @GetMapping("requests")
//    public List<MentorshipResponseDto> getMentorshipRequests(@RequestBody SearchDto searchDto){
//         List<MentorshipResponseDto> response = mentorshipRequestService.
//    }
}