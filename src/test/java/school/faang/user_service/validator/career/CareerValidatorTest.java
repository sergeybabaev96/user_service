package school.faang.user_service.validator.career;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CareerValidatorTest {

    private CareerDto careerDto;

    @BeforeEach
    public void setup() {
        careerDto = new CareerDto();
    }

    @Test
    public void testValidationCareerDtoWithFromIsNull() {
        careerDto.setFrom(null);
        assertThrows(DataValidationException.class, () -> CareerValidator.validate(careerDto));
    }

    @Test
    public void testCareerDtoWithFromMoreCurrentTime() {
        careerDto.setFrom(LocalDate.now().plusDays(1));
        assertThrows(DataValidationException.class, () -> CareerValidator.validate(careerDto));
    }

    @Test
    public void testValidationCareerDtoWithCompanyIsNull() {
        careerDto.setCompany(null);
        assertThrows(DataValidationException.class, () -> CareerValidator.validate(careerDto));
    }

    @Test
    public void testValidationCareerDtoWithCompanyIsBlamk() {
        careerDto.setCompany(" ");
        assertThrows(DataValidationException.class, () -> CareerValidator.validate(careerDto));
    }

    @Test
    public void testValidationCareerDtoWithPositionIsNull() {
        careerDto.setPosition(null);
        assertThrows(DataValidationException.class, () -> CareerValidator.validate(careerDto));
    }

    @Test
    public void testValidationCareerDtoWithPositionIsBlank() {
        careerDto.setPosition(" ");
        assertThrows(DataValidationException.class, () -> CareerValidator.validate(careerDto));
    }

}
