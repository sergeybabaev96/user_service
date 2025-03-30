package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validation.ValidId;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {

    private final MentorshipService mentorshipService;
    private final MentorshipMapper mentorshipMapper;

    @GetMapping("/mentors/{mentorId}/mentees")
    public List<MentorshipDto> getMentees(@PathVariable @ValidId Long mentorId) {
        return mentorshipService.getMentees(mentorId).stream()
                .map(mentorshipMapper::toDto)
                .toList();
    }

    @GetMapping("/mentees/{menteeId}/mentors")
    public List<MentorshipDto> getMentors(@PathVariable @ValidId Long menteeId) {
        return mentorshipService.getMentors(menteeId).stream()
                .map(mentorshipMapper::toDto)
                .toList();
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public ResponseEntity<Void> deleteMentee(@PathVariable @ValidId Long mentorId,
                                             @PathVariable @ValidId Long menteeId) {
        mentorshipService.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public ResponseEntity<Void> deleteMentor(@PathVariable @ValidId Long menteeId,
                                             @PathVariable @ValidId Long mentorId) {
        mentorshipService.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }
}
