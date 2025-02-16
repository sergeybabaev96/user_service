package school.faang.user_service.utils.event;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class EventPrepareData {
    public static Skill getSkill() {
        return Skill.builder()
                .id(1L)
                .build();
    }

    public static Skill getNewSkill() {
        return Skill.builder()
                .id(4L)
                .build();
    }

    public static Event getEventWithUserParticipatedEvents() {
        return Event.builder()
                .id(1L)
                .location("location")
                .owner(User.builder()
                        .id(2L)
                        .participatedEvents(List.of(
                                Event.builder()
                                        .id(1L)
                                        .build()
                        ))
                        .build())
                .relatedSkills(Arrays.asList(
                        Skill.builder()
                                .id(1L)
                                .build()
                ))
                .build();
    }

    public static Event getEvent() {
        return Event.builder()
                .id(1L)
                .location("location")
                .owner(User.builder()
                        .id(2L)
                        .build())
                .relatedSkills(Arrays.asList(
                        Skill.builder()
                                .id(1L)
                                .build()
                ))
                .build();
    }

    public static EventDto getEventDto() {
        return EventDto.builder()
                .id(1L)
                .ownerId(2L)
                .location("location")
                .relatedSkills(Arrays.asList(1L))
                .build();
    }

    public static User getUser() {
        return User.builder()
                .id(1L)
                .skills(Arrays.asList(
                        Skill.builder()
                                .id(1L)
                                .build(),
                        Skill.builder()
                                .id(2L)
                                .build(),
                        Skill.builder()
                                .id(3L)
                                .build()
                ))
                .participatedEvents(
                        List.of(
                                Event.builder()
                                        .id(1L)
                                        .build()
                        )
                )
                .build();
    }

    public static User getUserWithNoSkills() {
        return User.builder()
                .id(1L)
                .skills(Arrays.asList())
                .participatedEvents(
                        List.of(
                                Event.builder()
                                        .id(1L)
                                        .build()
                        )
                )
                .build();
    }


    public static EventFilterDto getFilterLocationDto() {
        return EventFilterDto.builder()
                .location("location")
                .build();
    }

    public static EventFilterDto getFilterOwnerDto() {
        return EventFilterDto.builder()
                .ownerId(1L)
                .build();
    }
}