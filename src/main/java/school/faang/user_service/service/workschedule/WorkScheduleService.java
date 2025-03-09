package school.faang.user_service.service.workschedule;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityAlreadyExistsException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        if (workScheduleRepository.existsById(workScheduleDto.id()))
            throw new EntityAlreadyExistsException("The work schedule already exists");
        return workScheduleMapper.toDto(workScheduleRepository.save(getInstance(userId, workScheduleDto)));
    }

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        long id = workScheduleDto.id();
        if (!workScheduleRepository.existsById(id))
            throw new EntityNotFoundException("The work schedule not found");
        workScheduleRepository.deleteById(id);
        return workScheduleMapper.toDto(workScheduleRepository.save(getInstance(userId, workScheduleDto)));
    }

    private WorkSchedule getInstance(long userId, WorkScheduleDto workScheduleDto) {
        if (!(workScheduleDto.startTime().isBefore(workScheduleDto.startLunch())
                && workScheduleDto.startLunch().isBefore(workScheduleDto.endLunch())
                && workScheduleDto.endLunch().isBefore(workScheduleDto.endTime())))
            throw new DataValidationException("The work schedule time is invalid");
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("User not founds");

        return WorkSchedule.builder()
                .startTime(workScheduleDto.startTime())
                .startLunch(workScheduleDto.startLunch())
                .endLunch(workScheduleDto.endLunch())
                .endTime(workScheduleDto.endTime())
                .user(userRepository.getReferenceById(userId))
                .build();
    }

    public WorkScheduleDto getWorkScheduleById(long id) {
        return workScheduleMapper.toDto(workScheduleRepository.getReferenceById(id));
    }
}