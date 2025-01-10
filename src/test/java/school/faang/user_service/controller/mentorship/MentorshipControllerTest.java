package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees() {
        Long id = 1L;
        List<Long> menteesIds = List.of(1L, 2L, 3L);
        when(mentorshipService.getMentees(id)).thenReturn(menteesIds);
        assertEquals(menteesIds, mentorshipController.getMentees(id));
    }
}
