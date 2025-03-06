package school.faang.user_service.validator;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDtoValidator {
    public static void validate(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Title can't be null or empty.");
        }
        if (eventDto.getStartDate() == null || eventDto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("StartDate can't be null or in the past.");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("OwnerId can't be null.");
        }
    }

    public static User validateOwnerAndSkills(EventDto eventDto, UserRepository userRepository) {
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException(
                        String.format("User with id %s not found", eventDto.getOwnerId()))
                );

        Set<Long> userSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> requiredSkillIds = Set.copyOf(eventDto.getRelatedSkills());

        if (!userSkillIds.containsAll(requiredSkillIds)) {
            throw new DataValidationException("User does not have the required skills for this event");
        }

        return owner;
    }

    public static List<Skill> validateAndGetRelatedSkills(EventDto eventDto, SkillRepository skillRepository) {
        if (eventDto.getRelatedSkills() == null || eventDto.getRelatedSkills().isEmpty()) {
            return List.of();
        }

        List<Skill> skills = skillRepository.findAllById(eventDto.getRelatedSkills());

        if (skills.size() != eventDto.getRelatedSkills().size()) {
            throw new DataValidationException("Some skills do not exist!");
        }

        return skills;
    }
}