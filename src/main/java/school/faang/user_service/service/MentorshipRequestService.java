package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipFilterDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipFilter;
import school.faang.user_service.mapper.MentorshipResponseMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.MentorshipValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipResponseMapper mentorshipResponseMapper;
    private final List<MentorshipValidator> validators;
    private final List<MentorshipFilter> filters;

    @Transactional
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto dto) {

        validators.forEach(validator -> validator.validate(dto));

        MentorshipRequest request = mentorshipResponseMapper.toRequestEntity(dto);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);

        return mentorshipResponseMapper.toResponseDto(savedRequest);
    }

    public List<MentorshipResponseDto> getRequests(MentorshipFilterDto filterDto) {
        Stream<MentorshipRequest> filteredRequests = mentorshipRequestRepository.findAll().stream();

        for (MentorshipFilter mentorshipFilter : filters) {
            if (mentorshipFilter.isApplicable(filterDto)) {
                filteredRequests = mentorshipFilter.apply(filteredRequests, filterDto);
            }
        }

        return filteredRequests
                .map(mentorshipResponseMapper::toResponseDto)
                .toList();
    }
}