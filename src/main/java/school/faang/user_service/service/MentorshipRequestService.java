package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void requestMentorship(MentorshipRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        User requester = userRepository.findById(requestDto.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        User receiver = userRepository.findById(requestDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (Objects.equals(requester.getId(), receiver.getId())) {
            throw new IllegalArgumentException("Requester and receiver cannot be the same person");
        }

        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(
                requestDto.getRequesterId(), requestDto.getReceiverId()
        );

        lastRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(3).isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("You can only request mentorship once every 3 months");
            }
        });

        mentorshipRequestRepository.create(requestDto.getRequesterId(), requestDto.getReceiverId(), requestDto.getDescription());
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto filter) {
        List<MentorshipRequest> requests = (List<MentorshipRequest>) mentorshipRequestRepository.findAll();

        if (filter.getDescription() != null) {
            requests = requests.stream()
                    .filter(request -> request.getDescription().contains(filter.getDescription()))
                    .collect(Collectors.toList());
        }
        if (filter.getRequesterId() != null) {
            requests = requests.stream()
                    .filter(request -> Objects.equals(request.getRequester().getId(), filter.getRequesterId()))
                    .collect(Collectors.toList());
        }
        if (filter.getReceiverId() != null) {
            requests = requests.stream()
                    .filter(request -> Objects.equals(request.getReceiver().getId(), filter.getReceiverId()))
                    .collect(Collectors.toList());
        }
        if (filter.getStatus() != null) {
            requests = requests.stream()
                    .filter(request -> request.getStatus() == filter.getStatus())
                    .collect(Collectors.toList());
        }

        return requests;
    }
}
