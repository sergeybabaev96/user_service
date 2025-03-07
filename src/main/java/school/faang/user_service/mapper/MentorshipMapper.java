package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {

    @Mapping(target = "userId", source = "entity.id")
    @Mapping(target = "userName", source = "entity.username")
    MentorshipDto toDto(User entity);

    @Mapping(target = "id", source = "dto.userId")
    @Mapping(target = "username", source = "dto.userName")
    @Mapping(target = "email", ignore = true) // Игнорируем поля, которых нет в DTO
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "aboutMe", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followees", ignore = true)
    @Mapping(target = "ownedEvents", ignore = true)
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "receivedMentorshipRequests", ignore = true)
    @Mapping(target = "sentMentorshipRequests", ignore = true)
    @Mapping(target = "sentGoalInvitations", ignore = true)
    @Mapping(target = "receivedGoalInvitations", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "participatedEvents", ignore = true)
    @Mapping(target = "recommendationsGiven", ignore = true)
    @Mapping(target = "recommendationsReceived", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "userProfilePic", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    @Mapping(target = "premium", ignore = true)
    @Mapping(target = "education", ignore = true)
    @Mapping(target = "career", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    User toEntity(MentorshipDto dto);

    List<MentorshipDto> toDtos(List<User> entities);
}
