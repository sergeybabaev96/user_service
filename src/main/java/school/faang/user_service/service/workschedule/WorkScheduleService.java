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

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto dto) {
        if (workScheduleRepository.existsById(dto.id()))
            throw new EntityAlreadyExistsException("The work schedule already exists");
        return save(userId, dto);
    }

    public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto dto) {
        if (!workScheduleRepository.existsById(dto.id()))
            throw new EntityNotFoundException("The work schedule not found");
        return save(userId, dto);
    }

    private WorkScheduleDto save(long userId, WorkScheduleDto dto) {
        return workScheduleMapper.toDto(workScheduleRepository.save(getInstance(userId, dto)));
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