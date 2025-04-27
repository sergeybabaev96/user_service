package school.faang.user_service.controller.avatar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.avatar.AvatarService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AvatarController.class)
public class AvatarControllerTest {
    private final static long VALID_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AvatarService avatarService;
    @MockBean
    private UserContext userContext;

    @DisplayName("Проверка успешной загрузки аватара пользователя при валидных данных")
    @Test
    public void givenValidUserIdAndFile_WhenAddUserAvatar_ThenSuccessRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        doNothing().when(avatarService).addUserAvatar(eq(VALID_USER_ID), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/{userId}/avatar", VALID_USER_ID)
                        .file(file)
                        .contentType("multipart/form-data"))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Проверка успешного получения аватара пользователя при валидном ID")
    @Test
    public void givenValidUserId_WhenGetUserAvatar_ThenSuccessRequest() throws Exception {
        Resource resource = new ByteArrayResource(new byte[]{1, 2, 3});

        when(avatarService.getUserAvatar(VALID_USER_ID)).thenReturn(resource);

        mockMvc.perform(get("/users/{userId}/avatar", VALID_USER_ID))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка успешного удаления аватара пользователя при валидном ID")
    @Test
    public void givenValidUserId_WhenDeleteUserAvatar_ThenSuccessRequest() throws Exception {
        doNothing().when(avatarService).deleteUserAvatar(VALID_USER_ID);

        mockMvc.perform(delete("/users/{userId}/avatar", VALID_USER_ID))
                .andExpect(status().isNoContent());
    }
}
