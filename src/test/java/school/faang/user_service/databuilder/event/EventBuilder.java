package school.faang.user_service.databuilder.event;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@UtilityClass
public class EventBuilder {

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
}
