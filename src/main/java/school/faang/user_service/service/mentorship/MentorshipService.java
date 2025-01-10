package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private MentorshipRepository mentorshipRepository;
    private UserMapper userMapper;


    public List<Long> getMentees(Long userId) {

        UserDto userDto = userMapper.toUserDto(mentorshipRepository.findById(userId).orElse(null));

        return userDto != null ? StreamSupport.stream(mentorshipRepository.findAllById(userDto.
                                getMenteesIds()).
                        spliterator(), false)
                .map(User::getId).toList() : Collections.emptyList();
    }
}
