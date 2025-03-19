package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EducationMapperTest {

    @Autowired
    private final EducationMapper educationMapper = Mappers.getMapper(EducationMapper.class);

    private EducationDto educationDto;
     private Education education;
    Integer dataYearFrom;
    Integer dataYearTo;

    @BeforeEach
    void setUp() {
        educationDto = new EducationDto();
        education = new Education();
        dataYearFrom = LocalDate.now().minusYears(2).getYear();
        dataYearTo = LocalDate.now().minusYears(1).getYear();
    }

    @Test
    public void testToEducation() {

        educationDto.setId(1L);
        educationDto.setYearFrom(dataYearFrom);
        educationDto.setYearTo(dataYearTo);
        educationDto.setInstitution("МГУ");
        educationDto.setEducationLevel("Высшее");
        educationDto.setSpecialization("Математика");

        Education education =  educationMapper.toEducation(educationDto);

        assertNotNull(educationDto);

        assertEquals(educationDto.getId(), education.getId());
        assertEquals(educationDto.getYearFrom(), education.getYearFrom());
        assertEquals(educationDto.getYearTo(), education.getYearTo());
        assertEquals(educationDto.getInstitution(), education.getInstitution());
        assertEquals(educationDto.getEducationLevel(), education.getEducationLevel());
        assertEquals(educationDto.getSpecialization(), education.getSpecialization());
    }

    @Test
    public void testToEducationDto() {

        education.setId(1L);
        education.setYearFrom(dataYearFrom);
        education.setYearTo(dataYearTo);
        education.setInstitution("МГУ");
        education.setEducationLevel("Высшее");
        education.setSpecialization("Математика");

        EducationDto educationDto = educationMapper.toEducationDto(education);

        assertNotNull(education);

        assertEquals(education.getId(), educationDto.getId());
        assertEquals(education.getYearFrom(), educationDto.getYearFrom());
        assertEquals(education.getYearTo(), educationDto.getYearTo());
        assertEquals(education.getInstitution(), educationDto.getInstitution());
        assertEquals(education.getEducationLevel(), educationDto.getEducationLevel());
        assertEquals(education.getSpecialization(), educationDto.getSpecialization());
    }
}