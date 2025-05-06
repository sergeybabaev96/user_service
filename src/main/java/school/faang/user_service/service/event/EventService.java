package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class EventService {
    private final SkillRepository skillRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventDto create(EventDto eventDto) {
        Optional.of(
                skillRepository.findAllByUserId(eventDto.getOwnerId()).stream()
                        .map(Skill::getId)
                        .collect(toSet())
                )
                .filter(ids -> ids.containsAll(eventDto.getRelatedSkillsIds()))
                .orElseThrow(() -> new DataValidationException("Owner doesn't have all related skills"));

        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId).orElseThrow(() ->
                new DataValidationException("Event not found")));
    }
}
