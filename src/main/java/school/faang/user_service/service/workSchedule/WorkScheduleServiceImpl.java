package school.faang.user_service.service.workSchedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import school.faang.user_service.service.WorkScheduleService;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleDto addWorkSchedule(Long userId, WorkScheduleDto workScheduleDto) {
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(String
                        .format("User with id %d was not found", userId)));
        workSchedule.setUser(user);
        workScheduleRepository.save(workSchedule);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    public WorkScheduleDto updateWorkSchedule(Long userId, WorkScheduleDto workScheduleDto) {
        WorkSchedule previousWorkSchedule = workScheduleRepository.findById(workScheduleDto.getId()).
                orElseThrow(IllegalArgumentException::new);
        if (!(userId == previousWorkSchedule.getUser().getId())) {
            throw new RuntimeException("You can change only your own schedule");
        }
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(String
                        .format("User with id %d was not found", userId)));
        workSchedule.setUser(user);
        workScheduleRepository.save(workSchedule);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    @Override
    public WorkScheduleDto getById(Long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                .orElseThrow(IllegalArgumentException::new);
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }
}
