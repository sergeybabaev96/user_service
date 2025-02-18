package school.faang.user_service.service.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.config.dicebear.DiceBearApiConfig;
import school.faang.user_service.config.dicebear.DicebearStyleGenerator;
import school.faang.user_service.config.dicebear.AvatarType;
import school.faang.user_service.exception.DiceBearException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiceBearServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private DiceBearApiConfig dicebearConfig;
    @Mock
    private DicebearStyleGenerator styleGenerator;
    @InjectMocks
    private DiceBearService service;
    private String filename;
    private String url;

    @BeforeEach
    void setUp() {
        filename = "Filename";
        url = "https://api.dicebear.com/9.x/bottts/jpeg?seed=Filename&width=400&height=400";
    }

    @Test
    void testGenerateAvatarThrowsException() {
        when(styleGenerator.getRandomStyleString()).thenReturn("bottts");
        when(restTemplate.exchange(url, HttpMethod.GET, null, byte[].class))
                .thenThrow(new RestClientException("Ex"));
        when(dicebearConfig.getApiUrl()).thenReturn("https://api.dicebear.com/9.x");

        DiceBearException exception = assertThrows(DiceBearException.class,
                () -> service.generateAvatar(filename, AvatarType.JPEG));

        assertEquals("Failed to generate avatar", exception.getMessage());

        verify(dicebearConfig, times(1)).getApiUrl();
        verify(restTemplate, times(1))
                .exchange(url, HttpMethod.GET, null, byte[].class);

    }

    @Test
    void testGenerateAvatarCorrect() {
        ResponseEntity<byte[]> response = new ResponseEntity<>(filename.getBytes(), HttpStatus.OK);
        when(styleGenerator.getRandomStyleString()).thenReturn("bottts");
        when(restTemplate.exchange(url, HttpMethod.GET, null, byte[].class))
                .thenAnswer(i -> response);
        when(dicebearConfig.getApiUrl()).thenReturn("https://api.dicebear.com/9.x");

        byte[] bytes = service.generateAvatar(filename, AvatarType.JPEG);

        assertArrayEquals(response.getBody(), bytes);

        verify(dicebearConfig, times(1)).getApiUrl();
        verify(restTemplate, times(1))
                .exchange(url, HttpMethod.GET, null, byte[].class);
    }
}