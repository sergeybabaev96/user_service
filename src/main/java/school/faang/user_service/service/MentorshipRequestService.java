package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filters.RequestFilter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;
    private final UserRepository userRepo;
    private final List<RequestFilter> filters;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto request) {
        if (!Objects.equals(request.getRequesterId(), request.getReceiverId())) {
            if (userRepo.findById(request.getRequesterId()).isEmpty() || userRepo.findById(request.getReceiverId()).isEmpty()) {
                throw new IllegalArgumentException("The user does not exist");
            }
            Optional<MentorshipRequest> optionalMentorshipRequest = mentorshipRequestRepository.findLatestRequest(request.getRequesterId(), request.getReceiverId());
            if (optionalMentorshipRequest.isPresent()) {
                MentorshipRequest mentorshipRequest = optionalMentorshipRequest.get();
                if (ChronoUnit.MONTHS.between(mentorshipRequest.getUpdatedAt(), LocalDate.now()) <= 3) {
                    User requester = userRepo.findById(request.getRequesterId()).get();
                    requester.getSentMentorshipRequests().add(mapper.toEntity(request));
                    User receiver = userRepo.findById(request.getReceiverId()).get();
                    receiver.getReceivedMentorshipRequests().add(mapper.toEntity(request));

                    mentorshipRequestRepository.create(request.getRequesterId(), request.getReceiverId(), request.getDescription());
                }
            } else {
                mentorshipRequestRepository.create(request.getRequesterId(), request.getReceiverId(), request.getDescription());
            }
            return request;
        } else {
            throw new IllegalArgumentException("The user cannot send a request to himself");
        }
    }

    public List<MentorshipRequestDto> getRequest(RequestFilterDto filter) {
        List<MentorshipRequest> listRequest = new ArrayList<>();
        Iterable<MentorshipRequest> iterable = mentorshipRequestRepository.findAll();
        iterable.forEach(listRequest::add);

        return getFilteredRequest(listRequest, filter).map(mapper::toDto).toList();
    }

    public Stream<MentorshipRequest> getFilteredRequest(List<MentorshipRequest> listRequest, RequestFilterDto filterDto) {
        Stream<MentorshipRequest> requestStream = listRequest.stream();
        for (RequestFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                requestStream = filter.apply(requestStream, filterDto);
            }
        }
        return requestStream;
    }

    public void acceptRequest(Long id) {
        Optional<MentorshipRequest> requestOptionalRequest = mentorshipRequestRepository.findById(id);
        if (requestOptionalRequest.isPresent()) {
            MentorshipRequest request = requestOptionalRequest.get();
            User requester = userRepo.findById(request.getRequester().getId()).get();
            User receiver = userRepo.findById(request.getReceiver().getId()).get();

            if (!receiver.getMentees().contains(receiver)) {
                request.setStatus(RequestStatus.ACCEPTED);//обновяться ли таким образом данные в бд
                receiver.getMentees().add(receiver);

                requester.getMentors().add(requester);
            } else {
                throw new IllegalArgumentException("You already have such a mentor.");
            }
        } else {
            throw new IllegalArgumentException("there is no such id");
        }

    }

    public void rejectRequest(Long id, RejectionDto rejection) {
        Optional<MentorshipRequest> requestOptionalRequest = mentorshipRequestRepository.findById(id);
        if (requestOptionalRequest.isPresent()) {
            MentorshipRequest request = requestOptionalRequest.get();
            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(rejection.reason());
        } else {
            throw new IllegalArgumentException("there is no such id");
        }
    }
}
