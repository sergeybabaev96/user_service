package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.WorkScheduleRepository;
import school.faang.user_service.validator.WorkScheduleValidator;


    @Service
    @RequiredArgsConstructor
    public class WorkScheduleService {
        private final UserRepository userRepository;
        private final WorkScheduleRepository workScheduleRepository;
        private final WorkScheduleMapper workScheduleMapper;

        @Transactional
        public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
            validateWorkSchedule(workScheduleDto);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException("User not found"));

            WorkSchedule workSchedule = WorkSchedule.builder()
                    .startTime(workScheduleDto.getStartTime())
                    .endTime(workScheduleDto.getEndTime())
                    .startLunch(workScheduleDto.getStartLunch())
                    .endLunch(workScheduleDto.getEndLunch())
                    .timezone(workScheduleDto.getTimezone())
                    .user(user)
                    .build();

            WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);
            return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
        }

        @Transactional
        public WorkScheduleDto updateWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
            validateWorkSchedule(workScheduleDto);

            WorkSchedule existingWorkSchedule = workScheduleRepository.findById(workScheduleDto.getId())
                    .orElseThrow(() -> new DataValidationException("Opening hours not found"));

            if (existingWorkSchedule.getUser().getId() != userId) {
                throw new DataValidationException("User mismatch");
            }

            WorkSchedule updatedWorkSchedule = WorkSchedule.builder()
                    .id(existingWorkSchedule.getId())
                    .startTime(workScheduleDto.getStartTime())
                    .endTime(workScheduleDto.getEndTime())
                    .startLunch(workScheduleDto.getStartLunch())
                    .endLunch(workScheduleDto.getEndLunch())
                    .timezone(workScheduleDto.getTimezone())
                    .user(existingWorkSchedule.getUser())
                    .build();

            WorkSchedule savedWorkSchedule = workScheduleRepository.save(updatedWorkSchedule);
            return workScheduleMapper.toWorkScheduleDto(savedWorkSchedule);
        }

        @Transactional(readOnly = true)
        public WorkScheduleDto getById(long workScheduleId) {
            WorkSchedule workSchedule = workScheduleRepository.findById(workScheduleId)
                    .orElseThrow(() -> new DataValidationException("Opening hours not found"));
            return workScheduleMapper.toWorkScheduleDto(workSchedule);
        }

        private void validateWorkSchedule(WorkScheduleDto workScheduleDto) {
            WorkScheduleValidator.validateWorkScheduleTimes(workScheduleDto);
        }
    }