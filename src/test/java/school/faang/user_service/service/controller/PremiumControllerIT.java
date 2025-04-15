package school.faang.user_service.service.controller;

import integration.kafka.FakePremiumListener;
import integration.kafka.TestKafkaPublisher;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.UserServiceApplication;
import school.faang.user_service.controller.PremiumController;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.service.premium.PremiumServiceImpl;
import school.faang.user_service.utils.JsonUtils;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                FakePremiumListener.class,
                UserServiceApplication.class,
                TestKafkaPublisher.class
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PremiumControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PremiumController premiumController;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private RestTemplate restTemplate;

    @SneakyThrows
    @Test
    @Transactional
    public void testBuyPremium_success() {
        PremiumRequestDto premiumRequest = new PremiumRequestDto(PremiumType.ONE_MONTH,
                1L, CurrencyDto.USD, true);

        mockMvc.perform(post("/api/v1/premium")
                        .contentType("application/json")
                        .content(jsonUtils.serialize(premiumRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.premiumType", is("ONE_MONTH")))
                .andExpect(jsonPath("$.paymentStatus", is("SUCCESS")))
                .andExpect(jsonPath("$.startDate").isNotEmpty())
                .andExpect(jsonPath("$.endDate").isNotEmpty());
    }
}
