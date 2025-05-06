package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Service
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;


    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository, MentorshipRequestMapper mentorshipRequestMapper) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.mentorshipRequestMapper = mentorshipRequestMapper;
    }

    public void requestMentorship(MentorshipRequestDto dto) {
        MentorshipRequest request = mentorshipRequestMapper.dtoToMentorshipRequest(dto);
        //какая-то валидация
        mentorshipRequestRepository.create(
                request.getRequester(),
                request.getReceiver(),
                request.getDescription()
        );
    }
}
