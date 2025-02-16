package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService userServiceMock;
    @InjectMocks
    UserController userController;

    @Mock
    private UsersService usersService;
    @InjectMocks
    private UserController userController;

    private MultipartFile file;

    @BeforeEach
    void setUp() {
        byte[] content = new byte[]{(byte) 0xe0, 0x4f, (byte) 0xd0,
                0x20, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2, (byte) 0xd8, 0x08, 0x00, 0x2b,
                0x30, 0x30, (byte) 0x9d};
        file = new MockMultipartFile("test", content);
    }

    @Test
    @DisplayName("Test import users")
    void testImportUsers() {
        userController.importUsers(file);
        Mockito.verify(userServiceMock, Mockito.times(1)).processPersonsFromFile(Mockito.any());
    }

    @Test
    @DisplayName("Test get user")
    void getUser() {
        Long userId = 1L;
        userController.getUser(userId);
        Mockito.verify(usersService, Mockito.times(1)).getUser(userId);
    }
}


