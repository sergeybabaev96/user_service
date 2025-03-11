package school.faang.user_service.service.goal;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoResponse;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.InvitationFilter;
import school.faang.user_service.service.goal.filter.InvitationFilterIdInvited;
import school.faang.user_service.service.goal.filter.InvitationFilterIdInviter;
import school.faang.user_service.service.goal.filter.InvitationFilterNameInvited;
import school.faang.user_service.service.goal.filter.InvitationFilterNameInviter;
import school.faang.user_service.service.goal.validator.InvitationDtoValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @Mock
    private InvitationDtoValidator invitationDtoValidator;

    private GoalInvitationDtoResponse goalInvitationDtoReject;
    private GoalInvitation goalInvitationReject;

    @BeforeEach
    public void setUp() {
        goalInvitationReject = new GoalInvitation();
        goalInvitationReject.setId(1L);
        goalInvitationReject.setStatus(RequestStatus.PENDING);

        Goal goal = new Goal();
        goalInvitationReject.setGoal(goal);
        goalInvitationDtoReject =
                new GoalInvitationDtoResponse(null, null, null, null, RequestStatus.REJECTED);


        List<InvitationFilter> filters = new ArrayList<>();
        filters.add(new InvitationFilterIdInvited());
        filters.add(new InvitationFilterIdInviter());
        filters.add(new InvitationFilterNameInvited());
        filters.add(new InvitationFilterNameInviter());

        goalInvitationService = new GoalInvitationServiceImpl(goalInvitationRepository, invitationDtoValidator,
                goalInvitationMapper, null, filters);
    }

    @Test
    public void testCreateInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(null, null, null);
        GoalInvitation goalInvitation = new GoalInvitation();
        GoalInvitation savedInvitation = new GoalInvitation();
        GoalInvitationDtoResponse savedDto = new GoalInvitationDtoResponse(null, null, null, null, null);

        when(goalInvitationMapper.toGoalInvitationEntity(goalInvitationDto)).thenReturn(goalInvitation);
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(savedInvitation);
        when(goalInvitationMapper.toGoalInvitationDtoResponse(savedInvitation)).thenReturn(savedDto);

        GoalInvitationDtoResponse result = goalInvitationService.createInvitation(goalInvitationDto);

        assertEquals(savedDto, result);

        verify(invitationDtoValidator, times(1)).validate(goalInvitationDto);
        verify(goalInvitationMapper, times(1)).toGoalInvitationEntity(goalInvitationDto);
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalInvitationMapper, times(1)).toGoalInvitationDtoResponse(savedInvitation);
    }

    @Test
    void testCreateInvitationValidationFails() {
        GoalInvitationDto inputDto = new GoalInvitationDto(null, null, null);

        doThrow(new IllegalArgumentException("Validation failed"))
                .when(invitationDtoValidator).validate(inputDto);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(inputDto)
        );

        assertEquals("Validation failed", exception.getMessage());
        verify(invitationDtoValidator).validate(inputDto);
        verifyNoInteractions(goalInvitationRepository);
        verifyNoInteractions(goalInvitationMapper);
    }

    @Test
    public void testRejectGoalInvitationSuccess() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitationReject));
        when(goalInvitationRepository.save(goalInvitationReject)).thenReturn(goalInvitationReject);
        when(goalInvitationMapper.toGoalInvitationDtoResponse(goalInvitationReject))
                .thenReturn(goalInvitationDtoReject);

        GoalInvitationDtoResponse result = goalInvitationService.rejectGoalInvitation(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.status());
    }

    @Test
    void testRejectGoalInvitationValidationFails() {
        GoalInvitationDto inputDto = new GoalInvitationDto(null, null, null);

        doThrow(new IllegalArgumentException("Validation failed"))
                .when(invitationDtoValidator).validate(inputDto);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(inputDto)
        );

        assertEquals("Validation failed", exception.getMessage());
        verify(invitationDtoValidator).validate(inputDto);
        verifyNoInteractions(goalInvitationRepository);
        verifyNoInteractions(goalInvitationMapper);
    }

    @Test
    void testGetInvitationsWithFiltersApplied() {
        final long idInviter1 = 1L;
        final long idInviter2 = 3L;
        final long idInvited1 = 2L;

        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setInviter(getUser(idInviter1));
        invitation1.setInvited(getUser(idInvited1));

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setInviter(getUser(idInviter1));
        invitation2.setInvited(getUser(idInvited1));

        GoalInvitation invitation3 = new GoalInvitation();
        invitation3.setInviter(getUser(idInviter2));
        invitation3.setInvited(getUser(idInvited1));

        List<GoalInvitation> invitations = List.of(invitation1, invitation2, invitation3);
        Mockito.when(goalInvitationRepository.findAll()).thenReturn(invitations);

        InvitationFilterDto filterDto = new InvitationFilterDto(null, null,
                idInviter1, idInvited1, null);
        List<GoalInvitationDtoResponse> result = goalInvitationService.getInvitations(filterDto);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testGetInvitationsWithFiltersAppliedSingleEntry() {

        long idInviter1 = 1L;
        long idInviter2 = 3L;
        long idInvited1 = 2L;

        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setInviter(getUser(idInviter1));
        invitation1.setInvited(getUser(idInvited1));

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setInviter(getUser(idInviter2));
        invitation2.setInvited(getUser(idInvited1));

        List<GoalInvitation> invitations = List.of(invitation1, invitation2);
        Mockito.when(goalInvitationRepository.findAll()).thenReturn(invitations);

        InvitationFilterDto filterDto = new InvitationFilterDto(null, null, idInviter1,
                idInvited1, null);
        List<GoalInvitationDtoResponse> result = goalInvitationService.getInvitations(filterDto);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testGetInvitationsWithNoApplicableFilters() {

        long idInviter1 = 1L;
        long idInviter2 = 3L;
        long idInvited1 = 2L;

        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setInviter(getUser(idInviter1));
        invitation1.setInvited(getUser(idInvited1));

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setInviter(getUser(idInviter2));
        invitation2.setInvited(getUser(idInvited1));

        List<GoalInvitation> invitations = List.of(invitation1, invitation2);
        Mockito.when(goalInvitationRepository.findAll()).thenReturn(invitations);

        InvitationFilterDto filterDto = new InvitationFilterDto(null, null,
                null, null, null);
        List<GoalInvitationDtoResponse> result = goalInvitationService.getInvitations(filterDto);

        Assertions.assertEquals(0, result.size());
    }

    @NotNull
    private static User getUser(long id) {
        User user1 = new User();
        user1.setId(id);
        return user1;
    }
}