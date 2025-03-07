package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.PersonCsvMapper;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class ParsingCvsUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PersonCsvMapper personCsvMapper;

    @InjectMocks
    private UserController userController;

    private final static String CSV_DATA = getCsvData();
    private final static List<UserDto> USER_DTOS = getUserDtos();
    private final static MultipartFile MULTIPART_FILE = getMultipartFile();

    @Test
    void createUsersFromCsvFilePositiveTest() throws IOException {
        when(personCsvMapper.toPersons(any(MultipartFile.class))).thenReturn(new ArrayList<Person>());
        when(userService.createUsers(any(List.class))).thenReturn(USER_DTOS);
        List<UserDto> result = userController.createUsersFromCsvFile(MULTIPART_FILE);

        assertEquals(2, result.size());
    }

    @Test
    void createUsersFromCsvFilePositiveWebTest() throws Exception {
        mockMvc.perform(
                post("/user/csv")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(MULTIPART_FILE.getBytes()))
                .andExpect(status().isOk());

        when(personCsvMapper.toPersons(any(MultipartFile.class))).thenReturn(new ArrayList<Person>());
        when(userService.createUsers(any(List.class))).thenReturn(USER_DTOS);
        List<UserDto> result = userController.createUsersFromCsvFile(MULTIPART_FILE);

        assertEquals(2, result.size());
    }

    @Test
    void createUsersFromCsvFileNegativeWebTest() throws Exception {
        mockMvc.perform(
                post("/user/csv")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUsersFromCsvFileNegative() throws IOException {
        when(personCsvMapper.toPersons(any(MultipartFile.class))).thenReturn(new ArrayList<Person>());
        when(userService.createUsers(any(List.class))).thenReturn(USER_DTOS);
        List<UserDto> result = userController.createUsersFromCsvFile(null);

        assertEquals(2, result.size());
    }


    private static String getCsvData() {
        return
        "firstName,lastName,email,phone,street,city,state,country,postalCode,faculty,yearsOfStudy,major,GPA,employer\n" +
        "John,Doe,john.doe@example.com,+123456789,123 Main St,New York,NY,USA,10001,Engineering,2010-2014,Computer Science,3.8,Google\n" +
        "Alice,Smith,alice.smith@example.com,+987654321,456 Oak St,Los Angeles,CA,USA,90001,Business,2012-2016,Marketing,3.5,Amazon";
    }

    private static List<UserDto> getUserDtos() {
        UserDto dto1 = new UserDto();
        dto1.setUsername("John Doe");
        dto1.setEmail("john.doe@example.com");

        UserDto dto2 = new UserDto();
        dto2.setUsername("Alice Smith");
        dto2.setEmail("alice.smith@example.com");

        return List.of(dto1, dto2);
    }

    private static MultipartFile getMultipartFile() {
        return new MockMultipartFile("file.txt", CSV_DATA.getBytes());
    }
}