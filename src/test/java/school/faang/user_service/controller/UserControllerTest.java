package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.FileData;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

    private FileData createFileData() throws IOException {
        return FileData.builder()
                .content(imageFile.getInputStream())
                .contentType(imageFile.getContentType())
                .build();
    }
}
