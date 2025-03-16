package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        careerDto.setFrom(LocalDate.of(2020, 1, 1));
        careerDto.setTo(LocalDate.of(2023, 1, 1));
        careerDto.setCompany("Tech Corp");
        careerDto.setPosition("Software Engineer");

        career = new Career();
        career.setId(1L);
        career.setDateFrom(LocalDate.of(2020, 1, 1));
        career.setDateTo(LocalDate.of(2023, 1, 1));
        career.setCompany("Tech Corp");
        career.setPosition("Software Engineer");
        career.setUser(user);
    }

    @Test
    void addCareer_ShouldAddCareer_WhenDataIsValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(careerMapper.toCareer(careerDto)).thenReturn(career);
        when(careerRepository.save(any(Career.class))).thenReturn(career);
        when(careerMapper.toCareerDto(career)).thenReturn(careerDto);

        CareerDto result = careerService.addCareer(1L, careerDto);

        assertNotNull(result);
        assertEquals("Tech Corp", result.getCompany());
        verify(careerRepository).save(any(Career.class));
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

        assertNotNull(result);
        assertEquals("Tech Corp", result.getCompany());
        verify(careerRepository).save(any(Career.class));
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
