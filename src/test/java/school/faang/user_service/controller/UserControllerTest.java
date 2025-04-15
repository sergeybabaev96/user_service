package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.FileData;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {UserController.class})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserContext userContext;

    private final MockMultipartFile imageFile = new MockMultipartFile(
            "file",
            "avatar.jpg",
            "image/jpeg",
            new byte[]{0x1, 0x2, 0x3}
    );

    @Test
    void testPositiveCreateUserAvatar() throws Exception {
        doNothing().when(userService).createUserAvatar(any(MultipartFile.class));

        mockMvc.perform(multipart("/users/avatar")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

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

    @Test
    void testPositiveGetUserAvatar() throws Exception {
        Long userId = 1L;
        FileData file = createFileData();
        when(userService.getUserAvatar(userId)).thenReturn(file);

        mockMvc.perform(get("/users/{userId}/avatar", userId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Objects.requireNonNull(imageFile.getContentType())))
                .andExpect(content().bytes(imageFile.getBytes()));
    }

    @Test
    void testPositiveRemoveUserAvatar() throws Exception {
        doNothing().when(userService).removeUserAvatar();

        mockMvc.perform(multipart("/users/avatar")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("DELETE");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    void positiveRegisterUsersFromFile() throws Exception {
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

        UserDto firstUser = createUserDto(1L, "John Doe", "john@example.com");
        UserDto secondUser = createUserDto(2L, "Jane Smith", "jane@example.com");
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

    private FileData createFileData() throws IOException {
        return FileData.builder()
                .content(imageFile.getInputStream())
                .contentType(imageFile.getContentType())
                .build();
    }

    private UserDto createUserDto(Long id, String username, String email) {
        return UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .active(true)
                .build();
    }
}
