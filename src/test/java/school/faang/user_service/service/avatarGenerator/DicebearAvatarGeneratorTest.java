package school.faang.user_service.service.avatarGenerator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.config.DicebearConfig;
import school.faang.user_service.exception.ExternalResourceNotFoundException;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DicebearAvatarGeneratorTest {

    public static final String TEST_STYLE_NAME = "adventurer";
    public static final String TEST_SEED = "emery";
    public static final String TEST_URI = "/%s/svg".formatted(TEST_STYLE_NAME);

    private WireMockServer wireMockServer;

    @MockBean
    private DicebearConfig dicebearConfig;

    @Autowired
    private DicebearAvatarGenerator dicebearAvatarGenerator;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();

        var baseUrl = "http://localhost:" + wireMockServer.port();

        var mockWebClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        dicebearAvatarGenerator = new DicebearAvatarGenerator(dicebearConfig, mockWebClient);

        when(dicebearConfig.getBaseUrl()).thenReturn(baseUrl);
        when(dicebearConfig.getStyleNames()).thenReturn(List.of(TEST_STYLE_NAME));
        when(dicebearConfig.getSeeds()).thenReturn(List.of(TEST_SEED));

        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testGetRandomAvatar_ServerReturnsSvg_Success() {
        // Arrange
        var testSvgContent =
                "<svg xmlns=\"http://www.w3.org/2000/svg/\" width=\"100\" height=\"100\">" +
                        "<circle cx=\"50\" cy=\"50\" r=\"40\" />" +
                        "</svg>";
        stubFor(get(urlPathEqualTo(TEST_URI))
                .withQueryParam(DicebearAvatarGenerator.SEED_QUERY_PARAMETER, equalTo(TEST_SEED))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "image/svg+xml")
                        .withBody(testSvgContent)));

        // Act
        var result = dicebearAvatarGenerator.getRandomAvatar();

        // Assert
        assertNotNull(result);

        var readedBytes = new byte[result.readableByteCount()];
        result.read(readedBytes);
        var resultContent = new String(readedBytes);
        assertEquals(testSvgContent, resultContent);

        verify(getRequestedFor(urlPathEqualTo(TEST_URI))
                .withQueryParam(DicebearAvatarGenerator.SEED_QUERY_PARAMETER, equalTo(TEST_SEED)));
    }

    @Test
    public void testGetRandomAvatar_ServerReturnsError_Throws() {
        // Arrange
        stubFor(get(urlPathEqualTo(TEST_URI))
                .withQueryParam(DicebearAvatarGenerator.SEED_QUERY_PARAMETER, equalTo(TEST_SEED))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Act + Assert
        assertThrows(
                ExternalResourceNotFoundException.class,
                () -> dicebearAvatarGenerator.getRandomAvatar());
        verify(getRequestedFor(urlPathEqualTo(TEST_URI))
                .withQueryParam(DicebearAvatarGenerator.SEED_QUERY_PARAMETER, equalTo(TEST_SEED)));
    }
}