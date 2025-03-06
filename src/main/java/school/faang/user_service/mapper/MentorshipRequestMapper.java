package school.faang.user_service.mapper;

import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class MentorshipRequestMapper {
    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "receiver", source = "receiverId")
    @Mapping(target = "status", constant = "PENDING")
    public abstract MentorshipRequest toEntity(MentorshipRequestDto dto);

    @Mapping(target = "status", constant = "REJECTED")
    public abstract void updateRequestFromDto(RejectionDto rejectionDto, MentorshipRequest mentorshipRequest);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    public abstract MentorshipRequestDto toDto(MentorshipRequest request);

    protected User mapUserIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }
}
