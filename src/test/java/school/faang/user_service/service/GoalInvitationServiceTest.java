package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static Goal mockGoal;
    private static User mockInviter;
    private static User mockInvited;
    private static GoalInvitation mockGoalInvitation;
    private static GoalInvitationDto mockGoalInvitationDto;


    @BeforeAll
    static void setUp() {
        mockGoal = new Goal();
        mockGoal.setId(1L);
        mockInviter = new User();
        mockInviter.setId(2L);
        mockInvited = new User();
        mockInvited.setId(3L);
        mockGoalInvitation = new GoalInvitation();
        mockGoalInvitation.setId(1L);
        mockGoalInvitation.setGoal(mockGoal);
        mockGoalInvitation.setInviter(mockInviter);
        mockGoalInvitation.setInvited(mockInvited);
        mockGoalInvitationDto = createGoalInvitationDto(mockGoal.getId(), mockInviter.getId(), mockInvited.getId());
    }

    private static GoalInvitationDto createGoalInvitationDto(Long goalId, Long inviterId, Long invitedId) {
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setId(1L);
        dto.setGoalId(goalId);
        dto.setInviterId(inviterId);
        dto.setInvitedId(invitedId);
        return dto;
    }

    @ParameterizedTest
    @CsvSource({
            ", 1, 1, 'Goal is required'",
            "1, , 1, 'InviterId is required'",
            "1, 1, , 'InvitedId is required'"
    })
    void testCreateMissingFields(Long goalId, Long inviterId, Long invitedId, String expectedMessage) {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(goalId, inviterId, invitedId);

        assertExceptionOnCreate(NullPointerException.class, goalInvitationDto, expectedMessage);
    }

    @Test
    void testCreateSameInviterAndInvited() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 1L, 1L);

        assertExceptionOnCreate(IllegalArgumentException.class, goalInvitationDto, "Inviter and invited must not be same person");
    }

    @Test
    void testCreateNoGoal() {
        when(goalService.findById(1L)).thenReturn(Optional.empty());
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 3L);

        assertExceptionOnCreate(EntityNotFoundException.class, goalInvitationDto, "Goal doesn't exist");
    }

    @Test
    void testCreateNoInviter() {
        when(goalService.findById(1L)).thenReturn(Optional.of(new Goal()));
        when(userService.findById(2L)).thenReturn(Optional.empty());
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 3L);

        assertExceptionOnCreate(EntityNotFoundException.class, goalInvitationDto, "Inviter doesn't exist");
    }

    @Test
    void testCreateNoInvited() {
        when(goalService.findById(1L)).thenReturn(Optional.of(new Goal()));
        when(userService.findById(2L)).thenReturn(Optional.of(new User()));
        when(userService.findById(3L)).thenReturn(Optional.empty());
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 3L);

        assertExceptionOnCreate(EntityNotFoundException.class, goalInvitationDto, "Invited doesn't exist");
    }

    @Test
    void testCreateSuccess() {
        when(goalInvitationMapper.toEntity(mockGoalInvitationDto)).thenReturn(mockGoalInvitation);
        when(goalService.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(userService.findById(2L)).thenReturn(Optional.of(mockInviter));
        when(userService.findById(3L)).thenReturn(Optional.of(mockInvited));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(mockGoalInvitation);
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(mockGoalInvitationDto);

        GoalInvitationDto createdDto = goalInvitationService.create(mockGoalInvitationDto);

        verify(goalInvitationRepository, times(1)).save(mockGoalInvitation);
        assertEquals(mockGoalInvitationDto.getGoalId(), createdDto.getGoalId());
    }

    @Test
    void testAcceptNoGoalInvitation() {
        Long idForSearch = mockGoalInvitation.getId();
        String expectedMessage = "GoalInvitation with id = " + idForSearch + " does not exist";
        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.empty());

        assertExceptionAccept(EntityNotFoundException.class, idForSearch, expectedMessage);
    }

    @Test
    void testAcceptNoGoal() {
        Long idForSearch = mockGoalInvitation.getId();
        String expectedMessage = "Goal with id = " + idForSearch + " does not exist";

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(mockGoalInvitation));
        when(goalService.findById(mockGoalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        assertExceptionAccept(EntityNotFoundException.class, idForSearch, expectedMessage);
    }

    @Test
    void testAcceptMaxGoalCountException() {
        Long idForSearch = mockGoalInvitation.getId();
        mockGoalInvitation.getInvited().setGoals(List.of(new Goal(), new Goal(), new Goal()));

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(mockGoalInvitation));
        when(goalService.findById(mockGoalInvitation.getGoal().getId())).thenReturn(Optional.of(mockGoal));

        assertExceptionAccept(IllegalStateException.class, idForSearch, "User has maximum goals");
    }

    @Test
    void testAcceptGoalAlreadyExistException() {
        Long idForSearch = mockGoalInvitation.getId();
        List<Goal> goals = new ArrayList<>();
        goals.add(mockGoal);
        mockGoalInvitation.getInvited().setGoals(goals);

        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(mockGoalInvitation));
        when(goalService.findById(mockGoalInvitation.getGoal().getId())).thenReturn(Optional.of(mockGoal));

        assertExceptionAccept(IllegalStateException.class, idForSearch, "User has this goal already");
    }

    @Test
    void testAcceptSuccess() {
        long idForSearch = mockGoalInvitation.getId();
        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(mockGoalInvitation));
        when(goalService.findById(mockGoalInvitation.getGoal().getId())).thenReturn(Optional.of(mockGoal));
        when(userService.save(mockInvited)).thenReturn(mockInvited);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(mockGoalInvitation);
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(mockGoalInvitationDto);

        goalInvitationService.accept(idForSearch);

        verify(userService, times(1)).save(mockInvited);
        verify(goalInvitationRepository, times(1)).save(mockGoalInvitation);
        assertEquals(1, mockGoalInvitation.getInvited().getGoals().size());
        assertEquals(RequestStatus.ACCEPTED, mockGoalInvitation.getStatus());
    }

    @Test
    void testRejectedSuccess() {
        long idForSearch = mockGoalInvitation.getId();
        when(goalInvitationRepository.findById(idForSearch)).thenReturn(Optional.of(mockGoalInvitation));
        when(goalService.findById(mockGoalInvitation.getGoal().getId())).thenReturn(Optional.of(mockGoal));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(mockGoalInvitation);
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(mockGoalInvitationDto);

        goalInvitationService.reject(idForSearch);

        verify(goalInvitationRepository, times(1)).save(mockGoalInvitation);
        assertEquals(RequestStatus.REJECTED, mockGoalInvitation.getStatus());
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

}
