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

    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto){
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        workSchedule.setUser(user);
        workScheduleRepository.save(workSchedule);

        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }
}
