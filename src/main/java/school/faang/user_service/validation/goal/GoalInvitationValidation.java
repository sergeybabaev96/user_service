package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidation {

    private static final int MAX_GOAL_USERS = 3;

    private final InvitationFilterDto filter = new InvitationFilterDto();
    private final UserService userService;
    private final GoalInvitationRepository goalInvitationRepository;


    public void checkAcceptingInvitation(Long id){
        goalExistsOrNot(id);
        invitedWorkingGoal(id);
        allowedNumberActiveGoals(id);
    }

    public void checkInvitation(GoalInvitationDto goalInvitationDto){
        userVerificationForSendingInvitation(goalInvitationDto);
        checkAvailabilityUserInDatabase(goalInvitationDto);
    }

    public void checkRejectingInvitation(Long id){
        goalExistsOrNot(id);
    }

    private void userVerificationForSendingInvitation(GoalInvitationDto goalInvitationDto){
        long inviterId = goalInvitationDto.getInviterId();
        long invitedId = goalInvitationDto.getInvitedUserId();

        if (inviterId == 0 || invitedId == 0 || inviterId == invitedId){
            String generalMessage = "В приглашении " + goalInvitationDto.getId() +
                    " неверно указан приглашающий и приглашенный пользователь";
            throw new BusinessException(generalMessage);
        }
    }

    private void checkAvailabilityUserInDatabase(GoalInvitationDto goalInvitationDto){
        long inviterId = goalInvitationDto.getInviterId();
        long invitedId = goalInvitationDto.getInvitedUserId();

        if (!userService.idVerificationUser(invitedId)){
            String message = "Пользователь c id" + invitedId + " не существует в базе";
            throw new EntityNotFoundException(message);
        };
        if (!userService.idVerificationUser(inviterId)){
            String message = "Пользователь c id" + inviterId + " не существует в базе";
            throw new EntityNotFoundException(message);
        }
    }

    private void allowedNumberActiveGoals(Long id){
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Объект не найден"));

        Long idUser = userService.getUserById(goalInvitation.getInvited().getId()).getId();

        long quantityActiveGoalUser = goalInvitationRepository.findAll().stream()
                .filter(element -> element.getInvited().getId().equals(idUser))
                .count();

        if (quantityActiveGoalUser <= MAX_GOAL_USERS) {
            String message = "Приглашенный пользователь к заявке " + id + ", подписан на максимальное количество целей";
            throw new BusinessException(message);
        }
    }

    private void invitedWorkingGoal(Long id){
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Объект не найден"));
        if (goalInvitation.getStatus().equals(RequestStatus.PENDING)){
            String message = "Пользователь " + goalInvitation.getInvited() + " уже работает с целью id: " + id;
            throw new BusinessException(message);
        }
    }

    private void goalExistsOrNot(Long id){
        if (goalInvitationRepository.existsById(id)){
            String message = "Цель с id: " + id + " не найдена";
            throw new EntityNotFoundException(message);
        }
    }
}
