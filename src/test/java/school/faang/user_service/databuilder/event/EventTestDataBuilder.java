package school.faang.user_service.databuilder.event;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@UtilityClass
public class EventTestDataBuilder {

    private static final LocalDateTime BASELINE_TIME = LocalDateTime.now().plusYears(1);

    public static Event createValidEvent(Long id, User owner) {
        return Event.builder()
                .id(id)
                .title("Java presentation")
                .startDate(BASELINE_TIME.plusDays(1))
                .endDate(BASELINE_TIME.plusDays(2))
                .owner(owner)
                .description("IT presentation")
                .relatedSkills(List.of(
                        Skill.builder().id(10L).title("Java").build(),
                        Skill.builder().id(20L).title("SQL").build()
                ))
                .location("Moscow")
                .maxAttendees(100)
                .type(EventType.PRESENTATION)
                .status(EventStatus.PLANNED)
                .build();
    }

    public static EventDto createValidEventDto(Long id) {
        return EventDto.builder()
                .id(id)
                .title("Java presentation")
                .startDate(BASELINE_TIME.plusDays(8))
                .endDate(BASELINE_TIME.plusDays(10))
                .ownerId(1L)
                .description("IT presentation")
                .relatedSkills(List.of(10L, 20L))
                .location("Moscow")
                .maxAttendees(100)
                .eventType(EventType.PRESENTATION)
                .eventStatus(EventStatus.PLANNED)
                .build();
    }

    public static EventDto createInvalidEventDto() {
        return EventDto.builder()
                .id(null)
                .title("Python presentation")
                .startDate(LocalDateTime.now().minusDays(3))
                .endDate(BASELINE_TIME.plusDays(10))
                .ownerId(1L)
                .description("IT presentation")
                .relatedSkills(List.of(10L, 20L))
                .location("London")
                .maxAttendees(50)
                .eventType(EventType.PRESENTATION)
                .eventStatus(EventStatus.PLANNED)
                .build();
    }

    public static Skill createValidSkill(Long id, String title) {
        return Skill.builder()
                .id(id)
                .title(title)
                .build();
    }

    public static User createValidUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }
}
