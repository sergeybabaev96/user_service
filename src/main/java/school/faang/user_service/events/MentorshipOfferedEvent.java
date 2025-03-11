package school.faang.user_service.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MentorshipOfferedEvent {
    private Long requesterId;
    private Long requestId;
    private Long mentorId;
}
