package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldDeactivateUser() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .username("John Doe")
                .email("john@example.com")
                .active(true)
                .build();
        Mockito.when(userService.deactivateUser(1L)).thenReturn(user);

        mockMvc.perform(put("/users/deactivate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldActivateUser() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .username("John Doe")
                .email("john@example.com")
                .active(true)
                .build();
        Mockito.when(userService.activateUser(1L)).thenReturn(user);

        mockMvc.perform(put("/users/activate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
