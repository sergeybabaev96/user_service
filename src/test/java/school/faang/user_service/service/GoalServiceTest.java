package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.impl.GoalServiceImpl;

import java.util.List;
import java.util.stream.Stream;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    @Test
    public void testGoalDeactivationForUser() {
        long userId = 1;

        when(goalRepository.deleteUserGoalByUserId(userId))
                .thenReturn(Stream.of(1L, 2L));
        when(goalRepository.existsOtherGoalsInProcess(anyLong(), eq(userId)))
                .thenReturn(false);

        goalService.deactivateGoalsByUserId(userId);

        verify(goalRepository, times(1)).deleteAllById(List.of(1L, 2L));
    }
}
