package school.faang.user_service.service.goal;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.configuration.appconfig.AppConfigService;
import school.faang.user_service.controller.goal.SortOption;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceImplTest {

    @Spy
    private GoalInvitationMapperImpl mapper;
    @Mock
    private UserService userService;
    @Mock
    private GoalService goalService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private AppConfigService appConfigService;
    @Mock
    private BooleanBuilderConstructor booleanBuilderConstructor;
    @InjectMocks
    private GoalInvitationServiceImpl service;

    private GoalInvitationDto goalInvitationDto;
    private User user;
    private User anotherUser;
    private Goal goal;
    private GoalInvitation goalInvitation;
    private GoalInvitation completeGoalInvitation;
    private GoalInvitationDto completeGoalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .inviterId(1L)
                .invitedId(2L)
                .goalId(1L)
                .build();

        user = User.builder()
                .id(goalInvitationDto.getInviterId())
                .username("username")
                .build();

        anotherUser = User.builder()
                .id(goalInvitationDto.getInvitedId())
                .username("username")
                .build();

        goal = Goal.builder()
                .id(goalInvitationDto.getGoalId())
                .title("goal title")
                .description("goal description")
                .invitations(new ArrayList<>())
                .users(new ArrayList<>())
                .build();

        goalInvitation = GoalInvitation.builder()
                .inviter(user)
                .invited(anotherUser)
                .goal(goal)
                .status(RequestStatus.PENDING)
                .build();

        completeGoalInvitation = GoalInvitation.builder()
                .id(1L)
                .inviter(user)
                .invited(anotherUser)
                .goal(goal)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        completeGoalInvitationDto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(user.getId())
                .invitedId(anotherUser.getId())
                .goalId(goal.getId())
                .build();
    }

    @Test
    void testCreateInvitation_whenValidDtoPassed_thenReturnGoalInvitation() {
        when(userService.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userService.findById(goalInvitationDto.getInvitedId())).thenReturn(anotherUser);
        when(goalService.findById(goalInvitationDto.getGoalId())).thenReturn(goal);
        when(mapper.toGoalInvitation(user, anotherUser, goal)).thenReturn(goalInvitation);
        when(mapper.toGoalInvitationDto(completeGoalInvitation)).thenReturn(completeGoalInvitationDto);
        when(goalInvitationRepository.save(any())).thenReturn(completeGoalInvitation);

        GoalInvitationDto savedInvitationDto = service.createInvitation(goalInvitationDto);

        assertTrue(goal.getInvitations().contains(goalInvitation));
        assertEquals(savedInvitationDto, completeGoalInvitationDto);
        verify(goalInvitationRepository, times(1)).save(any());
    }

    @Test
    void testCreateInvitation_whenGoalInvitationDtoIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.createInvitation(null));
    }

    @Test
    void testCreateInvitation_whenInviterAndInvitedAreTheSame_thenThrowIllegalArgumentException() {
        when(userService.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userService.findById(goalInvitationDto.getInvitedId())).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> service.createInvitation(goalInvitationDto));
        verify(goalInvitationRepository, times(0)).save(any());
    }

    @Test
    void testCreateInvitation_whenGoalIsNotFound_thenThrowIllegalArgumentException() {
        when(userService.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userService.findById(goalInvitationDto.getInvitedId())).thenReturn(anotherUser);
        when(goalService.findById(goalInvitationDto.getGoalId())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> service.createInvitation(goalInvitationDto));
        verify(goalInvitationRepository, times(0)).save(any());
    }

    @Test
    void testAcceptGoalInvitation() {
        anotherUser.setGoals(new ArrayList<>());
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));
        when(appConfigService.getLongOrDefault("max_active_goals", 3)).thenReturn(3L);

        service.acceptGoalInvitation(any(Long.class));
        assertEquals(RequestStatus.ACCEPTED, completeGoalInvitation.getStatus());
        assertTrue(goal.getUsers().contains(anotherUser));
        assertTrue(anotherUser.getGoals().contains(goal));
    }

    @Test
    void testAcceptGoalInvitation_whenGoalInvitationIsNotFound_thenThrowIllegalArgumentException() {
        when(goalInvitationRepository.findById(any(Long.class))).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> service.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testAcceptGoalInvitation_whenUserHasMoreThanMaxActiveGoals_thenThrowIllegalArgumentException() {
        anotherUser.setGoals(new ArrayList<>(List.of(goal, goal, goal, goal)));
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));
        when(appConfigService.getLongOrDefault("max_active_goals", 3)).thenReturn(3L);

        assertThrows(IllegalArgumentException.class, () -> service.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testAcceptGoalInvitation_whenUserAlreadyHasInvitedGoal_thenThrowIllegalArgumentException() {
        anotherUser.setGoals(List.of(goal));
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));
        when(appConfigService.getLongOrDefault("max_active_goals", 3)).thenReturn(3L);

        assertThrows(IllegalArgumentException.class, () -> service.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testRejectGoalInvitation_whenGoalInvitationIsNotFound_thenThrowIllegalArgumentException() {
        when(goalInvitationRepository.findById(any(Long.class))).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> service.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testRejectGoalInvitation() {
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));

        service.rejectGoalInvitation(any(Long.class));
        assertEquals(RequestStatus.REJECTED, completeGoalInvitation.getStatus());
    }

    @Test
    void testGetAllInvitations_whenNullPassed_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.getAllInvitations(null));
    }

    @Test
    void testGetAllInvitations() {
        InvitationFilterDto invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setSize(10);
        invitationFilterDto.setOffset(0);
        when(booleanBuilderConstructor.getQueryBooleanBuilder(any(InvitationFilterDto.class)))
                .thenReturn(new BooleanBuilder());
        when(goalInvitationRepository.findAll(any(Predicate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(completeGoalInvitation)));

        List<GoalInvitationDto> goalInvitations = service.getAllInvitations(invitationFilterDto);

        assertEquals(1, goalInvitations.size());
        assertEquals(completeGoalInvitation.getId(), goalInvitations.get(0).getId());
    }

    @Test
    void testGetAllInvitations_whenSortOptionIsPassedForFiltering_thenReturnSortedInvitations() {
        InvitationFilterDto invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setSize(10);
        invitationFilterDto.setOffset(0);
        invitationFilterDto.setSort(SortOption.STATUS);

        GoalInvitation acceptedInvitation = new GoalInvitation();
        acceptedInvitation.setId(1L);
        acceptedInvitation.setInviter(user);
        acceptedInvitation.setInvited(anotherUser);
        acceptedInvitation.setGoal(goal);
        acceptedInvitation.setStatus(RequestStatus.ACCEPTED);
        acceptedInvitation.setCreatedAt(LocalDateTime.now());
        acceptedInvitation.setUpdatedAt(LocalDateTime.now());

        when(booleanBuilderConstructor.getQueryBooleanBuilder(any(InvitationFilterDto.class)))
                .thenReturn(new BooleanBuilder());
        when(goalInvitationRepository.findAll(any(Predicate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(completeGoalInvitation, acceptedInvitation)));

        List<GoalInvitationDto> goalInvitations = service.getAllInvitations(invitationFilterDto);

        assertEquals(2, goalInvitations.size());
        assertEquals(RequestStatus.PENDING, goalInvitations.get(0).getStatus());
    }
}