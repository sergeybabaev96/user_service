package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/mentorships")
@Tag(name = "Mentorship request API", description = "API для управления запросами на менторство")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("")
    @Operation(summary = "Запросить менторство", description = "Делает запрос на менторство, на основе переданных данных")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto requestDto) {
        return mentorshipRequestService.requestMentorship(requestDto);
    }

    @GetMapping("/requests")
    @Operation(summary = "Показать активные запросы",
            description = "Выводит список всех запросов, на основе переданного фильтра")
    public List<MentorshipRequestDto> getRequests(@ModelAttribute RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/accept/{requestId}")
    @Operation(summary = "Принять запрос",
            description = "Принимает запрос на менторство с переданным идентификатором")
    public MentorshipRequestDto acceptRequest(
            @Parameter(description = "Идентификатор запроса") @PathVariable Long requestId) {
        return mentorshipRequestService.acceptRequest(requestId);
    }

    @PostMapping("/reject/{requestId}")
    @Operation(summary = "Отклонить запрос",
            description = "Отклоняет запрос на менторство с переданным идентификатором")
    public MentorshipRequestDto rejectRequest(@Parameter(description = "Идентификатор запроса")
                                                  @PathVariable Long requestId, @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}
