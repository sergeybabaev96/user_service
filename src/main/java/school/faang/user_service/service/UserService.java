package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.preson.PersonAboutDto;
import school.faang.user_service.dto.preson.PersonContactDto;
import school.faang.user_service.dto.preson.PersonDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.CsvMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CsvMapper csvMapper;
    private final UserMapper userMapper;
    private final GoalMapper goalMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final CountryRepository countryRepository;
    private final PasswordService passwordService;

    private static final int MONTHS = 3;

    @Value("$.{app.security.password-length}")
    private final int passwordLength;

    @Value("$.{app.security.password-length}")
    private final int minDataLength;

    public List<UserDto> registerUserFromFile(MultipartFile file) {
        List<String> validatedFile = validateAndReadFile(file);
        List<CompletableFuture<UserDto>> futures = new ArrayList<>();

        for (String line : validatedFile) {
            CompletableFuture<UserDto> future = CompletableFuture.supplyAsync(() -> registerUserFromLine(line));
            futures.add(future);
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private UserDto registerUserFromLine(String line) {
        String[] fields = line.split(",");
        if (fields.length < minDataLength) {
            throw new DataValidationException(
                    "Некорректный формат данных в файле, ожидается %d поля, получено %d",
                    minDataLength, fields.length);
        }

        PersonDto personDto = parsePersonDto(fields);
        PersonContactDto personContactDto = parsePersonContactDto(fields);
        PersonAboutDto personAboutDto = parsePersonAboutDto(fields);

        User user = csvMapper.toUser(personDto, personContactDto, personAboutDto);
        user.setPassword(passwordService.generateRandomPassword(passwordLength));
        userRepository.save(user);
        log.info("User with id: {} registered", user.getId());

        return userMapper.toDto(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        if (users.isEmpty()) {
            return Collections.emptyList();
        } else {
            return users.stream()
                    .map(userMapper::toDto)
                    .toList();
        }
    }

    private List<String> validateAndReadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("Файл пуст");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines().toList();
        } catch (IOException e) {
            throw new DataValidationException("Ошибка чтения файла");
        }
    }

    private PersonDto parsePersonDto(String[] fields) {
        return PersonDto.builder()
                .firstName(fields[0])
                .lastName(fields[1])
                .build();
    }


    private PersonContactDto parsePersonContactDto(String[] fields) {
        String countryTitle = fields[10];
        Country country = countryRepository.findByTitle(countryTitle)
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setTitle(countryTitle);
                    return countryRepository.save(newCountry);
                });

        return PersonContactDto.builder()
                .email(fields[5])
                .phone(fields[6])
                .city(fields[8])
                .country(country)
                .build();
    }


    private PersonAboutDto parsePersonAboutDto(String[] fields) {
        return PersonAboutDto.builder()
                .faculty(fields[12])
                .yearOfStudy(fields[13])
                .major(fields[14])
                .employer(fields[23])
                .build();
    }


    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        if (user.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(MONTHS))) {
            user.setActive(true);
        }

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        stopUserGoals(id);
        stopUserEvents(id);

        user.setActive(false);

        deleteMentorship(id);

        return userMapper.toDto(userRepository.save(user));
    }

    private void stopUserGoals(Long userId) {
        List<Long> userGoalsForDeleting = new ArrayList<>();
        List<Long> userGoalsForUpdating = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<GoalDto> allGoals = user.getGoals().stream().map(goalMapper::goalToGoalDto).toList();

        for (GoalDto goal : allGoals) {
            if (shouldGoalBeDeleted(goal, userId)) {
                userGoalsForDeleting.add(goal.id());
            } else {
                userGoalsForUpdating.add(goal.id());
            }
        }

        goalService.deleteAllByIds(userGoalsForDeleting);
        goalService.removeUserFromGoals(userGoalsForUpdating, userId);
    }

    private void stopUserEvents(Long userId) {
        List<Long> userEventsForDeleting = new ArrayList<>();
        List<Long> userEventsForUpdating = new ArrayList<>();

        List<EventDTO> allEvents = eventService.getParticipatedEvents(userId);

        for (EventDTO event : allEvents) {
            if (shouldEventBeDeleted(event, userId)) {
                userEventsForDeleting.add(event.getId());
            } else {
                userEventsForUpdating.add(event.getId());
            }
        }

        eventService.deleteAllByIds(userEventsForDeleting);
        eventService.removeUserFromEvents(userEventsForUpdating, userId);
    }

    private void deleteMentorship(Long userId) {
        mentorshipService.deleteMentorship(userId);
    }

    private boolean shouldGoalBeDeleted(GoalDto goal, Long userId) {
        List<Long> userIds = goal.userIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }

    private boolean shouldEventBeDeleted(EventDTO event, Long userId) {
        if (!Objects.equals(event.getOwnerId(), userId)) {
            return false;
        }

        List<Long> userIds = event.getAttendeesIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }
}
