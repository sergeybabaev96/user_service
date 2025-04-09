package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.UserController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserContext userContext;

    @Test
    public void positiveRegisterUsersFromFile() throws Exception {
        String csvContent = """
                John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA,10001,
                Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma,
                XYZ High School,2016,true,XYZ Technologies;
                """;


        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        UserDto firstUser = new UserDto(1L, "John Doe", "john@example.com", true);
        UserDto secondUser = new UserDto(2L, "Jane Smith", "jane@example.com", true);
        when(userService.registerUserFromFile(any(MultipartFile.class)))
                .thenReturn(List.of(firstUser, secondUser));

        mockMvc.perform(multipart("/users/register-from-file")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("John Doe"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(userService, times(1)).registerUserFromFile(any(MultipartFile.class));
    }

}
