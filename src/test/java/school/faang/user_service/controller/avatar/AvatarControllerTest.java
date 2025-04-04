package school.faang.user_service.controller.avatar;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.avatar.AvatarService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AvatarController.class)
public class AvatarControllerTest {

    @MockBean
    private AvatarService avatarService;
    @MockBean
    private UserContext userContext;
    long userId = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddUserAvatarReturnCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes()
        );

        doNothing().when(avatarService).addUserAvatar(anyLong(), any());

        mockMvc.perform(multipart("/api/v1/users/{userId}/avatar/new", userId)
                        .file(file))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetUserAvatarReturnInputStream() throws Exception {
        InputStream avatarStream = new ByteArrayInputStream("image".getBytes());
        when(avatarService.getUserAvatar(userId)).thenReturn(avatarStream);

        mockMvc.perform(get("/api/v1/users/{userId}/avatar/get", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveUserAvatar() throws Exception {
        doNothing().when(avatarService).removeUserAvatar(userId);

        mockMvc.perform(delete("/api/v1/users/{userId}/avatar", userId))
                .andExpect(status().isNoContent());
    }

}
