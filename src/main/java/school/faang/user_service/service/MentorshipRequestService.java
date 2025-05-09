package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipResponseMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final long TIME_FOR_REQUEST = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipResponseMapper mentorshipResponseMapper;


    @Transactional
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto dto) {

        if (!mentorshipRepository.existsById(dto.getRequester()) || !mentorshipRepository.existsById(dto.getReceiver())
        ) {
            throw new ResponseStatusException(NOT_FOUND, "RequesterId or receiverId not found");
        }
        if (dto.getRequester() == dto.getReceiver()) {
            throw new ResponseStatusException(BAD_REQUEST, "Requester and receiver are the same person.");
        }
        mentorshipRequestRepository.findLatestRequest(dto.getRequester(), dto.getReceiver())
                .map(MentorshipRequest::getCreatedAt)
                .ifPresent(createdAt -> {
                    if (createdAt.isAfter(LocalDateTime.now().minusMonths(TIME_FOR_REQUEST))) {
                        throw new ResponseStatusException(BAD_REQUEST, "You have already made a request during this period");
                    }
                });

        MentorshipRequest request = mentorshipResponseMapper.toRequestEntity(dto);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);

        return mentorshipResponseMapper.toResponseDto(savedRequest);
    }
}
