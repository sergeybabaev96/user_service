package school.faang.user_service.filter.goal.invitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InviterNameFilterTest extends InvitationFilterTest {

    @BeforeEach
    void setUp() {
        invitationFilter = new InviterNameFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrueWhenNameIsNotNull() {
        filters.setInviterNamePattern("Jane Smith");

        boolean result = invitationFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenNameIsNull() {
        boolean result = invitationFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterInvitationsByMatchingName() {
        filters.setInviterNamePattern("Jane Smith");

        invitation1 = new GoalInvitation();
        invitation1.setInviter(new User());
        invitation1.getInviter().setId(1L);
        invitation1.getInviter().setUsername("Jane Smith");

        invitation2 = new GoalInvitation();
        invitation2.setInviter(new User());
        invitation2.getInviter().setId(2L);
        invitation2.getInviter().setUsername("John Smith");

        invitation3 = new GoalInvitation();
        invitation3.setInviter(new User());
        invitation3.getInviter().setId(3L);
        invitation3.getInviter().setUsername("Jane Smith");

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        List<GoalInvitation> expected = List.of(invitation1, invitation3);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertEquals(expected, result);
    }

    @Test
    void apply_ShouldReturnEmptyStreamWhenNoInvitationsMatch() {
        filters.setInviterNamePattern("Jane Doe");

        invitation1 = new GoalInvitation();
        invitation1.setInviter(new User());
        invitation1.getInviter().setId(1L);
        invitation1.getInviter().setUsername("Jane Smith");

        invitation2 = new GoalInvitation();
        invitation2.setInviter(new User());
        invitation2.getInviter().setId(2L);
        invitation2.getInviter().setUsername("John Smith");

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}