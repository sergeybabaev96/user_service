package school.faang.user_service.service.rating;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.rating.UserRatingTypeRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUserRatingTypeServiceTest {
    @Mock
    private UserRatingTypeRepository userRatingTypeRepository;
    @InjectMocks
    private RatingTypeService ratingTypeService;

    @Test
    void findAll() {
        List<UserRatingType> userRatingTypes = List.of(
                UserRatingType.builder()
                        .id(1L)
                        .name("Skill rating")
                        .isActivity(true)
                        .cost(10)
                        .build(),
                UserRatingType.builder()
                        .id(2L)
                        .name("Views rating")
                        .isActivity(false)
                        .cost(7)
                        .build()
        );

        when(userRatingTypeRepository.findAll()).thenReturn(userRatingTypes);
        Assertions.assertEquals(userRatingTypes, ratingTypeService.findAll());
        verify(userRatingTypeRepository, times(1)).findAll();
    }

    @Test
    void findByName() {
        UserRatingType userRatingType = UserRatingType.builder()
                .id(1L)
                .name("Skill rating")
                .isActivity(true)
                .cost(10)
                .build();
        when(userRatingTypeRepository.findByName(anyString())).thenReturn(userRatingType);
        Assertions.assertEquals(userRatingType, ratingTypeService.findByName("Skill rating"));
        verify(userRatingTypeRepository, times(1)).findByName(anyString());
    }

    @Test
    void findByNameNotFound() {
        when(userRatingTypeRepository.findByName(anyString())).thenReturn(null);
        Assertions.assertThrows(DataValidationException.class, () -> ratingTypeService.findByName(""));
        verify(userRatingTypeRepository, times(1)).findByName(anyString());
    }

    @Test
    void add() {
        UserRatingType userRatingType = UserRatingType.builder()
                .id(2L)
                .name("Skill rating")
                .isActivity(true)
                .cost(10)
                .build();
        when(userRatingTypeRepository.save(userRatingType)).thenReturn(userRatingType);
        Assertions.assertEquals(userRatingType, ratingTypeService.add(userRatingType));
        verify(userRatingTypeRepository, times(1)).save(userRatingType);
    }

    @Test
    void addUserRatingTypeIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> ratingTypeService.add(null));
    }

    @Test
    void updateCost() {
        UserRatingType source = UserRatingType.builder()
                .id(2L)
                .name("Skill rating")
                .isActivity(true)
                .cost(10)
                .build();
        when(userRatingTypeRepository.findById(2L)).thenReturn(Optional.of(source));

        UserRatingType updated = UserRatingType.builder()
                .id(2L)
                .name("Skill rating")
                .isActivity(true)
                .cost(9)
                .build();

        UserRatingType expected = UserRatingType.builder()
                .id(2L)
                .name("Skill rating")
                .isActivity(true)
                .cost(9)
                .build();

        when(userRatingTypeRepository.save(updated)).thenReturn(updated);

        UserRatingType actual = ratingTypeService.updateCost(2L, 9);

        Assertions.assertEquals(expected, actual);
        verify(userRatingTypeRepository, times(1)).save(updated);
        verify(userRatingTypeRepository, times(1)).findById(2L);
    }

    @Test
    void updateCostIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> ratingTypeService.updateCost(null, 9));
    }

    @Test
    void updateCostCostIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> ratingTypeService.updateCost(9L, null));
    }

    @Test
    void findById() {
        UserRatingType userRatingType = UserRatingType.builder()
                .id(2L)
                .name("Skill rating")
                .isActivity(true)
                .cost(9)
                .build();

        when(userRatingTypeRepository.findById(2L)).thenReturn(Optional.of(userRatingType));

        Assertions.assertEquals(userRatingType, ratingTypeService.findById(2L));
        verify(userRatingTypeRepository, times(1)).findById(2L);
    }

    @Test
    void findByIdIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> ratingTypeService.findById(null));
    }

    @Test
    void findByIdIdNotFound() {
        when(userRatingTypeRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> ratingTypeService.findById(2L));
    }
}