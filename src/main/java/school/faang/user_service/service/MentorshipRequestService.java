package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;
    private final UserRepository userRepository;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            throw new EntityNotFoundException("Пользователь с id " + mentorshipRequestDto.getRequesterId()
                    + " не существует.");
        }
        if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            throw new EntityNotFoundException("Пользователь с id " + mentorshipRequestDto.getReceiverId()
                    + " не существует.");
        }
        if (mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            throw new BusinessException("Нельзя отправить запрос самому себе.");
        }
        Optional<MentorshipRequest> latestRequest =
                mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
        if (latestRequest.isPresent()) {
            MentorshipRequest request = latestRequest.get();
            LocalDateTime requestDate = request.getCreatedAt();
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime threeMonthsAgo = currentDate.minusMonths(3);

            if (!requestDate.isBefore(threeMonthsAgo)) {
                throw new BusinessException("Нельзя отправлять запрос чаще чем раз в три месяца.");
            }
        }
        MentorshipRequest entity = mapper.toEntity(mentorshipRequestDto);
        if (entity.getRequester() == null || entity.getReceiver() == null) {
            throw new BusinessException("Отправитель или получатель запроса не указан.");
        }
        mentorshipRequestRepository.save(entity);
        return mapper.toDto(entity);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        List<MentorshipRequest> allRequests = mentorshipRequestRepository.findAll();
        List<MentorshipRequest> filteredRequests = allRequests.stream()
                .filter(request -> requestFilterDto.getDescription() == null ||
                        request.getDescription().toLowerCase().contains(requestFilterDto.getDescription().toLowerCase()))
                .filter(request -> requestFilterDto.getRequesterId() == null ||
                        request.getRequester().getId().equals(requestFilterDto.getRequesterId()))
                .filter(request -> requestFilterDto.getReceiverId() == null ||
                        request.getReceiver().getId().equals(requestFilterDto.getReceiverId()))
                .filter(request -> requestFilterDto.getStatus() == null ||
                        request.getStatus().equals(requestFilterDto.getStatus()))
                .collect(Collectors.toList());
        return filteredRequests.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство не найден."));

        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new BusinessException("Запрос уже принят.");
        }
        User requester = request.getRequester();
        User receiver = request.getReceiver();
        if (requester.getMentors().contains(receiver)) {
            throw new BusinessException("Пользователь уже является вашим ментором.");
        }
        requester.getMentors().add(receiver);
        userRepository.save(requester);

        request.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest updatedRequest = mentorshipRequestRepository.save(request);
        return mapper.toDto(updatedRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство не найден."));

        if (RequestStatus.REJECTED.equals(request.getStatus())) {
            throw new BusinessException("Запрос уже отклонен.");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getRejectionReason());
        MentorshipRequest updatedStatus = mentorshipRequestRepository.save(request);
        return mapper.toDto(updatedStatus);
    }
}

