package school.faang.user_service.service.avatarGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.config.DicebearConfig;
import school.faang.user_service.exception.ExternalResourceNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DicebearAvatarGenerator implements AvatarGeneratorService {
    public static final String SEED_QUERY_PARAMETER = "seed";

    private final DicebearConfig dicebearConfig;

    @Qualifier("dicebearWebClient")
    private final WebClient webClient;

    @Override
    public byte[] getRandomAvatar() throws IOException {
        var styleName = getRandomElementFromList(dicebearConfig.getStyleNames());
        var seed = getRandomElementFromList(dicebearConfig.getSeeds());

        var imageBuffer = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("%s/svg".formatted(styleName))
                        .queryParam(SEED_QUERY_PARAMETER, seed)
                        .build())
                .retrieve()
                .bodyToMono(DataBuffer.class)
                .onErrorMap(ex -> {
                    var errorMessage = "Cannot get image from DiceBear server (styleName = %s, seed = %s): %s".formatted(
                            styleName,
                            seed,
                            ex.getMessage());
                    log.error(errorMessage, ex);

                    return new ExternalResourceNotFoundException(errorMessage);
                })
                .block();

        if (imageBuffer == null) {
            throw new ExternalResourceNotFoundException(
                    "Cannot get image from DiceBear server (styleName = %s, seed = %s)".formatted(
                    styleName,
                    seed));
        }

        try (var stream = imageBuffer.asInputStream()) {
            return stream.readAllBytes();
        } finally {
            DataBufferUtils.release(imageBuffer);
        }
    }

    private String getRandomElementFromList(List<String> strings) {
        var index = ThreadLocalRandom.current().nextInt(strings.size());

        return strings.get(index);
    }
}
