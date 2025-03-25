package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import school.faang.user_service.config.avatar.UserAvatarProperties;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.user.UserAvatarService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(UserAvatarController.class)
class UserAvatarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAvatarService avatarService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UserAvatarProperties avatarProperties;

    private MockMultipartFile avatarFile;

    @BeforeEach
    void setup() {
        avatarFile = new MockMultipartFile(
                "avatarFile",
                "avatar.jpg",
                "image/jpeg",
                "image content".getBytes()
        );
    }

    @Test
    void testUploadAvatarSuccess() throws Exception {

        when(userContext.getUserId()).thenReturn(1L);
        when(avatarProperties.getSizeMB()).thenReturn(5);
        doNothing().when(avatarService).uploadAvatar(anyLong(), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/v1/user/avatar")
                        .file(avatarFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadAvatarFileSizeExceeded() throws Exception {

        when(userContext.getUserId()).thenReturn(1L);
        when(avatarProperties.getSizeMB()).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/v1/user/avatar")
                        .file("avatarFile", new byte[3 * 1024 * 1024])
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Maximum upload size of 1048576 bytes exceeded")));
    }

    @Test
    void testGetAvatarByUserSuccess() throws Exception {

        when(userContext.getUserId()).thenReturn(1L);
        InputStream mockStream = new ByteArrayInputStream(new byte[10]);
        when(avatarService.getAvatarByUserId(1L)).thenReturn(mockStream);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/user/avatar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void testGetAvatarByKeySuccess() throws Exception {

        InputStream mockStream = new ByteArrayInputStream(new byte[10]);
        when(avatarService.getAvatarByKey("testKey")).thenReturn(mockStream);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/user/avatar/{key}", "testKey"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void testDeleteAvatarSuccess() throws Exception {

        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(avatarService).deleteAvatar(anyLong());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/v1/user/avatar"))
                .andExpect(status().isOk());
    }
}

