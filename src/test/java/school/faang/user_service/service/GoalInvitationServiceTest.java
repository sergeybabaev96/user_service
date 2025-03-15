package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @Spy
    private GoalInvitationMapper goalInvitationMapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Captor
    private ArgumentCaptor<GoalInvitation> goalInvitationCaptor;

    @Captor
    private ArgumentCaptor<User> invitedUserCaptor;

    private static Stream<Arguments> provideInvalidGoalInvitationInputs() {
        return Stream.of(
                Arguments.of(null, 1L, 1L, "Goal is required"),
                Arguments.of(1L, null, 1L, "InviterId is required"),
                Arguments.of(1L, 1L, null, "InvitedId is required")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidGoalInvitationInputs")
    @DisplayName("Negative: error when required fields are missing")
    void testCreateNegativeMissingFields(Long goalId, Long inviterId, Long invitedId, String expectedMessage) {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(goalId, inviterId, invitedId);

        assertExceptionOnCreate(NullPointerException.class, goalInvitationDto, expectedMessage);
    }

    @Test
    @DisplayName("Negative: error when inviter and invited have the same value")
    void testCreateNegativeSameInviterAndInvited() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 1L, 1L);

        assertExceptionOnCreate(IllegalArgumentException.class, goalInvitationDto, "Inviter and invited must not be same person");
    }

    @Test
    @DisplayName("Negative: error when goal value is missing")
    void testCreateNegativeNoGoal() {
        when(goalService.findById(1L)).thenReturn(Optional.empty());
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 3L);

        assertExceptionOnCreate(EntityNotFoundException.class, goalInvitationDto, "Goal doesn't exist");
    }

    @Test
    @DisplayName("Negative: error when inviter value is missing")
    void testCreateNegativeNoInviter() {
        when(goalService.findById(1L)).thenReturn(Optional.of(new Goal()));
        when(userService.findById(2L)).thenReturn(Optional.empty());
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 3L);

        assertExceptionOnCreate(EntityNotFoundException.class, goalInvitationDto, "Inviter doesn't exist");
    }

    @Test
    @DisplayName("Negative: error when invited value is missing")
    void testCreateNegativeNoInvited() {
        when(goalService.findById(1L)).thenReturn(Optional.of(new Goal()));
        when(userService.findById(2L)).thenReturn(Optional.of(new User()));
        when(userService.findById(3L)).thenReturn(Optional.empty());
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 3L);

        assertExceptionOnCreate(EntityNotFoundException.class, goalInvitationDto, "Invited doesn't exist");
    }

    @Test
    @DisplayName("Positive: successful creation of GoalInvitation")
    void testCreateSuccess() {
        GoalInvitation goalInvitation = createGoalInvitation();
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(goalInvitation);

        when(goalInvitationMapper.toEntity(goalInvitationDto)).thenReturn(goalInvitation);
        when(goalService.findById(1L)).thenReturn(Optional.of(goalInvitation.getGoal()));
        when(userService.findById(2L)).thenReturn(Optional.of(goalInvitation.getInviter()));
        when(userService.findById(3L)).thenReturn(Optional.of(goalInvitation.getInvited()));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(goalInvitationDto);

        GoalInvitationDto createdDto = goalInvitationService.create(goalInvitationDto);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        assertEquals(goalInvitationDto.getGoalId(), createdDto.getGoalId());
    }

    @Test
    @DisplayName("Negative: error when goalInvitation value is missing")
    void testAcceptNegativeNoGoalInvitation() {
        Long idForSearch = 1L;
        String expectedMessage = "GoalInvitation with id = " + idForSearch + " does not exist";
        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.empty());

        assertExceptionAccept(EntityNotFoundException.class, idForSearch, expectedMessage);
    }

    @Test
    @DisplayName("Negative: error when goal value is missing")
    void testAcceptNegativeNoGoal() {
        GoalInvitation goalInvitation = createGoalInvitation();
        Long idForSearch = goalInvitation.getId();
        String expectedMessage = "Goal with id = " + idForSearch + " does not exist";

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(goalInvitation));
        when(goalService.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        assertExceptionAccept(EntityNotFoundException.class, idForSearch, expectedMessage);
    }

    @Test
    @DisplayName("Negative: error when goals exceeds max limit")
    void testAcceptNegativeMaxGoalCountExceeded() {
        GoalInvitation goalInvitation = createGoalInvitation();
        Long idForSearch = goalInvitation.getId();
        goalInvitation.getInvited().setGoals(List.of(createGoal(1L), createGoal(2L), createGoal(3L)));

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(goalInvitation));
        when(goalService.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        assertExceptionAccept(IllegalStateException.class, idForSearch, "User has maximum goals");
    }

    @Test
    @DisplayName("Negative: error when trying to accept al already existing goal")
    void testAcceptNegativeGoalAlreadyExists() {
        GoalInvitation goalInvitation = createGoalInvitation();
        Long idForSearch = goalInvitation.getId();
        List<Goal> goals = List.of(goalInvitation.getGoal());
        goalInvitation.getInvited().setGoals(goals);

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(goalInvitation));
        when(goalService.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        assertExceptionAccept(IllegalStateException.class, idForSearch, "User has this goal already");
    }

    @Test
    @DisplayName("Positive: goal accepted successfully")
    void testAcceptSuccess() {
        GoalInvitation goalInvitation = createGoalInvitation();
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(goalInvitation);
        long idForSearch = goalInvitation.getId();

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(goalInvitation));
        when(goalService.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));
        when(userService.save(goalInvitation.getInvited())).thenReturn(goalInvitation.getInvited());
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(goalInvitationDto);

        goalInvitationService.accept(idForSearch);

        verify(userService, times(1)).save(goalInvitation.getInvited());
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        assertEquals(1, goalInvitation.getInvited().getGoals().size());
        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
    }

    @Test
    @DisplayName("Positive: goal rejected successfully")
    void testRejectedSuccess() {
        GoalInvitation goalInvitation = createGoalInvitation();
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(goalInvitation);
        long idForSearch = goalInvitation.getId();

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(goalInvitation));
        when(goalService.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(goalInvitationDto);

        goalInvitationService.reject(idForSearch);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
    }

    void assertExceptionOnCreate(Class<? extends Exception> expectedException, GoalInvitationDto goalInvitationDto, String ExpectedMessage) {
        var exception = assertThrows(
                expectedException,
                () -> goalInvitationService.create(goalInvitationDto)
        );
        assertEquals(ExpectedMessage, exception.getMessage());
    }

    void assertExceptionAccept(Class<? extends Exception> expectedException, Long id, String ExpectedMessage) {
        var exception = assertThrows(
                expectedException,
                () -> goalInvitationService.accept(id)
        );
        assertEquals(ExpectedMessage, exception.getMessage());
    }

    private Goal createGoal(Long id) {
        Goal goal = new Goal();
        goal.setId(id);
        return goal;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private GoalInvitation createGoalInvitation() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(createGoal(1L));
        goalInvitation.setInviter(createUser(2L));
        goalInvitation.setInvited(createUser(3L));
        return goalInvitation;
    }

    private GoalInvitationDto createGoalInvitationDto(Long goalId, Long inviterId, Long invitedId) {
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setId(1L);
        dto.setGoalId(goalId);
        dto.setInviterId(inviterId);
        dto.setInvitedId(invitedId);
        return dto;
    }

    private GoalInvitationDto createGoalInvitationDto(GoalInvitation goalInvitation) {
        return createGoalInvitationDto(
                goalInvitation.getGoal().getId(),
                goalInvitation.getInviter().getId(),
                goalInvitation.getInvited().getId()
        );
    }
}
