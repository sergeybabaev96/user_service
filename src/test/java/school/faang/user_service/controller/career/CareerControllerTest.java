package school.faang.user_service.controller.career;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.career.CareerService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CareerController.class)
public class CareerControllerTest {

    @MockBean
    private CareerService careerService;
    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;


    long userId = 1L;
    long careerId = 1L;

    CareerDto prepareCareerDto() {
        CareerDto careerDto = new CareerDto();
        careerDto.setId(careerId);
        careerDto.setFrom(LocalDate.of(2006, 1, 1));
        careerDto.setTo(java.time.LocalDate.of(2012, 1, 1));
        careerDto.setCompany("TestCompany");
        careerDto.setPosition("testPosition");
        return careerDto;
    }

    @Test
    void testAddCareerReturnCareerDTO() throws Exception {
        CareerDto careerDto = prepareCareerDto();
        String careerJson = objectMapper.writeValueAsString(careerDto);

        when(careerService.addCareer(anyLong(), any(CareerDto.class))).thenReturn(careerDto);

        mockMvc.perform(post("/careers")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(careerJson))
                .andExpect(status().isOk());

        verify(careerService, times(1)).addCareer(eq(userId), any(CareerDto.class));
    }

    @Test
    void testUpdateCareerById() throws Exception {
        CareerDto careerDto = prepareCareerDto();
        String careerJson = objectMapper.writeValueAsString(careerDto);
        when(careerService.updateCareer(anyLong(), any(CareerDto.class))).thenReturn(careerDto);

        mockMvc.perform(put("/careers/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(careerJson)
        ).andExpect(status().isOk());

        verify(careerService, times(1)).updateCareer(anyLong(), any(CareerDto.class));
    }

    @Test
    void testGettingCareerById() throws Exception {
        CareerDto careerDto = prepareCareerDto();
        when(careerService.getById(careerId)).thenReturn(careerDto);
        mockMvc.perform(get("/careers/{careerId}", careerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(careerId));

        verify(careerService, times(1)).getById(careerId);
    }

}
