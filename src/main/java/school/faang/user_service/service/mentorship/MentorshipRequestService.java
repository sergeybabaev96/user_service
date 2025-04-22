package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.properties.EventType;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipService mentorshipService;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    @Transactional
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        existsById(mentorshipRequestDto.getMentorId(), "Mentor");
        existsById(mentorshipRequestDto.getMenteeId(), "Mentee");
        validateMentorHasNotMentee(mentorshipRequestDto.getMentorId(), mentorshipRequestDto.getMenteeId());
        mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDto.getMenteeId(), mentorshipRequestDto.getMentorId())
                .ifPresentOrElse( existing -> createMentorshipRequest(mentorshipRequestDto, existing),
                        () -> mentorshipRequestRepository.create(
                                mentorshipRequestDto.getMenteeId(),
                                mentorshipRequestDto.getMentorId(),
                                mentorshipRequestDto.getDescription()

                        ));
        validateMenteeIsNotMentor(mentorshipRequestDto);
    }

    @Transactional
    public void acceptMentorshipRequest(long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship request with id " + id + " not found"));
        validateMentorHasNotMentee(mentorshipRequest.getReceiver().getId(), mentorshipRequest.getRequester().getId());
        mentorshipRequest.getRequester().getMentors().add(mentorshipRequest.getReceiver());
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        mentorshipAcceptedEventPublisher.publish(MentorshipAcceptedEventDto.builder()
                .requestId(id).requesterId(mentorshipRequest.getRequester().getId())
                .receiverId(mentorshipRequest.getReceiver().getId())
                .eventType(EventType.MENTORSHIP_ACCEPTED).build());
    }

    private void existsById(long id, String owner) {
        if (!mentorshipRepository.existsById(id)) {
            throw new EntityNotFoundException(owner + " с id " + id + " не найден в базе!");
        }
    }

    private void throwDataValidationException(long mentorId, long menteeId) {
        throw new DataValidationException("Mentor with id " + mentorId + " has mentee with id " + menteeId);
    }

    private void validateMentorHasNotMentee(long mentorId, long menteeId) {
        mentorshipService.getMentees(mentorId).stream()
                .filter(mentee -> mentee.getId() == menteeId)
                .forEach(mentee -> throwDataValidationException(mentorId, menteeId));
    }

    private void createMentorshipRequest(MentorshipRequestDto mentorshipRequestDto, MentorshipRequest mentorshipRequest) {
        LocalDateTime lastDate = LocalDateTime.now().minusMonths(3);
        if (mentorshipRequest.getCreatedAt().isBefore(lastDate)) {
            mentorshipRequestRepository.create(mentorshipRequestDto.getMenteeId(),
                    mentorshipRequestDto.getMentorId(), mentorshipRequestDto.getDescription());
        }
    }

    private void validateMenteeIsNotMentor(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getMentorId() == mentorshipRequestDto.getMenteeId()) {
            throw new DataValidationException("Mentee shouldn't be mentor");
        }
    }


}