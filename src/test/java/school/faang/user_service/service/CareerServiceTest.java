package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.career.CareerService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private CareerMapper careerMapper;

    @InjectMocks
    private CareerService careerService;

    private CareerDto careerDto;
    private Career career;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        careerDto = new CareerDto();
        careerDto.setId(1L);
        careerDto.setFrom(LocalDate.of(2025, 3, 13));
        careerDto.setTo(LocalDate.of(2024, 12, 1));
        careerDto.setCompany("Супер корпорация");
        careerDto.setPosition("Инженер программист");

        career = new Career();
        career.setId(1L);
        career.setDateFrom(LocalDate.of(2025, 3, 13));
        career.setDateTo(LocalDate.of(2024, 12, 1));
        career.setCompany("Супер корпорация");
        career.setPosition("Инженер программист");
        career.setUser(user);
    }

    @Test
    void addCareer_ShouldAddCareer_WhenDataIsValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(careerMapper.toCareer(careerDto)).thenReturn(career);
        when(careerRepository.save(any(Career.class))).thenReturn(career);
        when(careerMapper.toCareerDto(career)).thenReturn(careerDto);

        CareerDto result = careerService.addCareer(1L, careerDto);
        ArgumentCaptor<Career> careerCaptor = ArgumentCaptor.forClass(Career.class);
        verify(careerRepository).save(careerCaptor.capture());

        Career savedCareer = careerCaptor.getValue();

        assertNotNull(savedCareer);
        assertEquals("Супер корпорация", savedCareer.getCompany());
        assertEquals("Инженер программист", savedCareer.getPosition());
        assertEquals(user, savedCareer.getUser());

    }

    @Test
    void addCareer_ShouldThrowException_WhenDateIsInFuture() {
        careerDto.setFrom(LocalDate.now().plusDays(1));

        assertThrows(DataValidationException.class, () -> careerService.addCareer(1L, careerDto));
    }

    @Test
    void updateCareer_ShouldUpdateCareer_WhenDataIsValid() {
        when(careerRepository.findById(1L)).thenReturn(Optional.of(career));
        when(careerRepository.save(any(Career.class))).thenReturn(career);
        when(careerMapper.toCareerDto(career)).thenReturn(careerDto);

        CareerDto result = careerService.updateCareer(1L, careerDto);
        ArgumentCaptor<Career> careerCaptor = ArgumentCaptor.forClass(Career.class);
        verify(careerRepository).save(careerCaptor.capture());

        Career updatedCareer = careerCaptor.getValue();

        assertNotNull(updatedCareer);
        assertEquals("Супер корпорация", updatedCareer.getCompany());
        assertEquals("Инженер программист", updatedCareer.getPosition());
        assertEquals(user, updatedCareer.getUser());
    }

    @Test
    void updateCareer_ShouldThrowException_WhenUserIdDoesNotMatch() {
        career.setUser(new User());
        career.getUser().setId(2L);

        when(careerRepository.findById(1L)).thenReturn(Optional.of(career));

        assertThrows(DataValidationException.class, () -> careerService.updateCareer(1L, careerDto));
    }

    @Test
    void getById_ShouldReturnCareerDto_WhenCareerExists() {
        when(careerRepository.findById(1L)).thenReturn(Optional.of(career));
        when(careerMapper.toCareerDto(career)).thenReturn(careerDto);

        CareerDto result = careerService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenCareerNotFound() {
        when(careerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> careerService.getById(1L));
    }
}
