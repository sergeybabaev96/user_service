package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class EventDtoValidator {
    private static final String ERROR_TITLE_NULL_OR_EMPTY = "Validation failed: Title can't be null or empty";
    private static final String ERROR_START_DATE_INVALID = "Validation failed: StartDate can't be null or in the past";
    private static final String ERROR_OWNER_ID_NULL = "Validation failed: OwnerId can't be null";
    private static final String ERROR_EVENT_NULL = "Validation failed: EventDto is null";

    private static final String ERROR_USER_NOT_FOUND = "User with id {} not found. Requested at: {}";
    private static final String ERROR_SKILLS_NOT_FOUND = "User with id {} does not have the required skills for this event. Requested at: {}";
    private static final String USER_NOT_FOUND_EXCEPTION = "User with id %s not found";
    private static final String USER_NOT_HAVE_SKILLS = "User with id %s does not have skills";

    private static final String USER_HAVE_EXIST_SKILLS = "Some skills do not exist.";
    private static final String ERROR_MISSING_SKILLS = "Some skills do not exist. Missing: {}. Requested at: {}";


    public static void validate(EventDto eventDto) {
        LocalDateTime currentTime = LocalDateTime.now();

        if (eventDto == null) {
            log.error("{} | {}", currentTime, ERROR_EVENT_NULL);
            throw new DataValidationException("EventDto can't be null.");
        }

        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            log.error("{} | {}", currentTime, ERROR_TITLE_NULL_OR_EMPTY);
            throw new DataValidationException(ERROR_TITLE_NULL_OR_EMPTY);
        }
        if (eventDto.getStartDate() == null || eventDto.getStartDate().isBefore(currentTime)) {
            log.error("{} | {}", currentTime, ERROR_START_DATE_INVALID);
            throw new DataValidationException(ERROR_START_DATE_INVALID);
        }
        if (eventDto.getOwnerId() == null) {
            log.error("{} | {}", currentTime, ERROR_OWNER_ID_NULL);
            throw new DataValidationException(ERROR_OWNER_ID_NULL);
        }
    }

    public static User validateOwnerAndSkills(EventDto eventDto, UserRepository userRepository) {
        LocalDateTime currentTime = LocalDateTime.now();

        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> {
                            log.error(ERROR_USER_NOT_FOUND, eventDto.getOwnerId(), currentTime);
                            return new DataValidationException(String.format(USER_NOT_FOUND_EXCEPTION, eventDto.getOwnerId()));
                        }
                );

        Set<Long> userSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> requiredSkillIds = Set.copyOf(eventDto.getRelatedSkills());

        if (!userSkillIds.containsAll(requiredSkillIds)) {
            log.error(ERROR_SKILLS_NOT_FOUND, eventDto.getOwnerId(), currentTime);
            throw new DataValidationException(String.format(USER_NOT_HAVE_SKILLS, eventDto.getOwnerId()));
        }

        return owner;
    }

    public static List<Skill> validateAndGetRelatedSkills(EventDto eventDto, SkillRepository skillRepository) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (eventDto.getRelatedSkills() == null || eventDto.getRelatedSkills().isEmpty()) {
            log.info("Validation skipped: No skills required for event {}. Timestamp: {}", eventDto.getId(), currentTime);
            return List.of();
        }

        List<Skill> skills = skillRepository.findAllById(eventDto.getRelatedSkills());

        Set<Long> foundSkillIds = skills.stream().map(Skill::getId).collect(Collectors.toSet());
        Set<Long> missingSkills = eventDto.getRelatedSkills().stream()
                .filter(id -> !foundSkillIds.contains(id))
                .collect(Collectors.toSet());

        if (skills.size() != eventDto.getRelatedSkills().size()) {
            log.error(ERROR_MISSING_SKILLS, missingSkills, currentTime);
            throw new DataValidationException(USER_HAVE_EXIST_SKILLS);
        }

        return skills;
    }
}