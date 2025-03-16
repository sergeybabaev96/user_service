package school.faang.user_service.service.career;

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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareerServiceTest {

    @InjectMocks
    private CareerService careerService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CareerRepository careerRepository;

    @Spy
    private CareerMapperImpl careerMapperImp;

    @Captor
    private ArgumentCaptor<Career> careerCaptor;

    private final Long userId = 1L;
    private final User user = new User();
    private final CareerDto careerDto = new CareerDto();
    private final Career career = new Career();
    private final Long careerId = 1L;

    @BeforeEach
    public void setUp() {
        careerDto.setId(1L);
        careerDto.setFrom(LocalDate.now().minusYears(2));
        careerDto.setTo(LocalDate.now());
        careerDto.setCompany("TestCompany");
        careerDto.setPosition("TestPosition");
        user.setId(userId);
    }

    public void prepareCareerData() {
        career.setId(careerId);
        career.setDateFrom(careerDto.getFrom());
        career.setDateTo(careerDto.getTo());
        career.setCompany(careerDto.getCompany());
        career.setPosition(careerDto.getPosition());
        career.setUser(user);
    }

    @Test
    public void testFindUserByIdInvalid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> careerService.addCareer(userId, careerDto));
    }

    @Test
    public void testAddCareerSaveCareer() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        careerService.addCareer(userId, careerDto);

        verify(careerRepository, times(1)).save(careerCaptor.capture());
        Career career = careerCaptor.getValue();
        assertEquals(careerDto.getId(), career.getId());
        assertEquals(careerDto.getFrom(), career.getDateFrom());
        assertEquals(careerDto.getTo(), career.getDateTo());
        assertEquals(careerDto.getCompany(), career.getCompany());
        assertEquals(careerDto.getPosition(), career.getPosition());
    }

    @Test
    public void testFindCareerByIdInvalid() {
        when(careerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> careerService.updateCareer(userId, careerDto));
    }

    @Test
    public void usersIdUpdateCareerNotEquals() {
        career.setUser(user);
        long invalidUserId = userId + 1;
        when(careerRepository.findById(anyLong())).thenReturn(Optional.of(career));

        assertThrows(DataValidationException.class, () -> careerService.updateCareer(invalidUserId, careerDto));
    }

    @Test
    public void testUpdateCareerSaveCareer() {
        prepareCareerData();
        when(careerRepository.findById(careerDto.getId())).thenReturn(Optional.of(career));

        careerService.updateCareer(userId, careerDto);

        verify(careerRepository, times(1)).save(careerCaptor.capture());
        Career careerSaved = careerCaptor.getValue();
        assertEquals(careerDto.getId(), careerSaved.getId());
        assertEquals(careerDto.getFrom(), careerSaved.getDateFrom());
        assertEquals(careerDto.getTo(), careerSaved.getDateTo());
        assertEquals(careerDto.getCompany(), careerSaved.getCompany());
        assertEquals(careerDto.getPosition(), careerSaved.getPosition());
    }

    @Test
    public void testGetByIdCareerNotFound() {
        when(careerRepository.findById(careerId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> careerService.getById(careerId));
    }

    @Test
    public void testGetByIdCareer() {
        prepareCareerData();
        when(careerRepository.findById(careerId)).thenReturn(Optional.of(career));

        CareerDto result = careerService.getById(careerId);

        assertEquals(career.getId(), result.getId());
        assertEquals(career.getDateFrom(), result.getFrom());
        assertEquals(career.getDateTo(), result.getTo());
        assertEquals(career.getCompany(), result.getCompany());
        assertEquals(career.getPosition(), result.getPosition());

        verify(careerMapperImp, times(1)).toCareerDto(career);
    }

}
