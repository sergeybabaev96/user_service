package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipResponseMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.MentorshipValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipResponseMapper mentorshipResponseMapper;
    private final List<MentorshipValidator> validators;

    @Transactional
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto dto) {

        validators.forEach(validator -> validator.validate(dto));

        MentorshipRequest request = mentorshipResponseMapper.toRequestEntity(dto);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);

        return mentorshipResponseMapper.toResponseDto(savedRequest);
    }
}
