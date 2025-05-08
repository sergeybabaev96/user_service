package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        List<Long> userSkills = skillRepository.findAllByUserId(event.getOwnerId())
                .stream().map(Skill::getId)
                .toList();

        boolean hasAllSkills = userSkills.containsAll(event.getRelatedSkills());
        if (!hasAllSkills) {
            throw new DataValidationException("Пользователь не обладает всеми требуемыми навыками");
        }

        Event saved = eventRepository.save(eventMapper.toEntity(event));
        return eventMapper.toDto(saved);
    }
}
