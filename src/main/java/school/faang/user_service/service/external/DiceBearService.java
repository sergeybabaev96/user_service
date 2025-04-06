package school.faang.user_service.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import school.faang.user_service.config.dicebear.DiceBearApiConfig;
import school.faang.user_service.config.dicebear.DicebearStyleGenerator;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.exception.DiceBearException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiceBearService {

    private final RestTemplate diceBearRestTemplate;
    private final DiceBearApiConfig diceBearApiConfig;
    private final DicebearStyleGenerator styleGenerator;

    @Retryable(value = { DiceBearException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public byte[] generateAvatar(AvatarType type) {
        String style = styleGenerator.getRandomStyleString();
        String seed = UUID.randomUUID().toString(); // можно поменять на username

        String url = UriComponentsBuilder
                .fromHttpUrl(diceBearApiConfig.getApiUrl())
                .pathSegment(style)
                .pathSegment(type.name().toLowerCase()) // png, jpeg, svg
                .queryParam("seed", seed)
                .queryParam("width", type.getWidth())
                .queryParam("height", type.getHeight())
                .toUriString();

        log.info("Fetching avatar from URL: {}", url);

        try {
            return diceBearRestTemplate.getForObject(url, byte[].class);
        } catch (Exception e) {
            log.error("Failed to fetch avatar from DiceBear", e);
            throw new DiceBearException("Не удалось сгенерировать аватар", e);
        }
    }
}