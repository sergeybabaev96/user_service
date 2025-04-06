package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapperImpl;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareerServiceTest {
    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CareerRepository careerRepository;

    @Spy
    private CareerMapperImpl careerMapper;

    @Captor
    private ArgumentCaptor<Career> careerCaptor;

    private User user;
    private User secondUser;
    private Career existingCareer;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        secondUser = new User();
        secondUser.setId(2L);

        existingCareer = new Career();
        existingCareer.setId(1L);
        existingCareer.setDateFrom(LocalDate.now().minusYears(4));
        existingCareer.setCompany("CompanyA");
        existingCareer.setPosition("Senior Developer");
        existingCareer.setUser(user);
    }

    private CareerDto createCareerDto(long id, LocalDate fromDate) {
        return new CareerDto(id, fromDate, null, "CompanyX", "Junior Dev");
    }

    @Test
    void shouldThrowExceptionWhenCreateWithFutureDate() {
        CareerDto careerDto = createCareerDto(1L, LocalDate.now().plusDays(11));
        assertThrows(DataValidationException.class, () -> careerService.addCareer(1L, careerDto));
    }

    @Test
    void shouldThrowExceptionWhenUserIdNotFound() {
        long userId = 1L;
        CareerDto careerDto = createCareerDto(1L, LocalDate.now().minusDays(5));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> careerService.addCareer(userId, careerDto));
    }

    @Test
    void shouldSuccessfullyAddCareer() {
        CareerDto careerDto = createCareerDto(1L, LocalDate.now().minusDays(5));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        careerService.addCareer(user.getId(), careerDto);
        verify(careerRepository, times(1)).save(careerCaptor.capture());

        Career career = careerCaptor.getValue();
        assertEquals(careerDto.from(), career.getDateFrom());
        assertEquals(careerDto.company(), career.getCompany());
        assertEquals(careerDto.position(), career.getPosition());
    }

    @Test
    void shouldThrowExceptionWhenCareerNotFoundForUpdate() {
        long careerId = 1L;
        when(careerRepository.findById(careerId)).thenReturn(Optional.empty());
        CareerDto careerDto = createCareerDto(1L, LocalDate.now().minusDays(5));
        assertThrows(EntityNotFoundException.class, () -> careerService.updateCareer(1L, careerDto));
    }

    @Test
    void shouldThrowExceptionWhenCareerIdsMismatch() {
        long careerId = 1L;
        existingCareer.setUser(secondUser);

        when(careerRepository.findById(careerId)).thenReturn(Optional.of(existingCareer));

        CareerDto careerDto = createCareerDto(careerId, LocalDate.now().minusDays(5));

        assertThrows(DataValidationException.class, () -> careerService.updateCareer(user.getId(), careerDto));
        verify(careerRepository, times(0)).save(any(Career.class));
    }

    @Test
    void shouldSuccessfullyUpdateCareer() {
        CareerDto careerDto = createCareerDto(1L, LocalDate.now().minusYears(4));

        when(careerRepository.findById(careerDto.id())).thenReturn(Optional.of(existingCareer));

        careerService.updateCareer(user.getId(), careerDto);
        verify(careerRepository, times(1)).save(careerCaptor.capture());
        Career updatedCareer = careerCaptor.getValue();

        assertEquals(careerDto.id(), updatedCareer.getId());
        assertEquals(careerDto.from(), updatedCareer.getDateFrom());
        assertEquals(careerDto.company(), updatedCareer.getCompany());
        assertEquals(careerDto.position(), updatedCareer.getPosition());
    }

    @Test
    void shouldThrowExceptionWhenCareerNotFoundForGetById() {
        long careerId = 1;
        when(careerRepository.findById(careerId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> careerService.getById(careerId));
    }

    @Test
    void shouldSuccessfullyGetCareerById() {
        long careerId = existingCareer.getId();
        CareerDto expectedCareerDto = new CareerDto(careerId, existingCareer.getDateFrom(), null, "CompanyA", "Senior Developer");

        when(careerRepository.findById(careerId)).thenReturn(Optional.of(existingCareer));

        CareerDto result = careerService.getById(careerId);

        assertEquals(expectedCareerDto.id(), result.id());
        assertEquals(expectedCareerDto.from(), result.from());
        assertEquals(expectedCareerDto.company(), result.company());
        assertEquals(expectedCareerDto.position(), result.position());
    }
}
