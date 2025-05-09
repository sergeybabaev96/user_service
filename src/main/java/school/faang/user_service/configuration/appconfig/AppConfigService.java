package school.faang.user_service.configuration.appconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@Slf4j
public class AppConfigService {
    private final AppConfigRepository appConfigRepository;
    private final Map<String, String> cachedAppConfigs = new ConcurrentHashMap<>();

    public AppConfigService(AppConfigRepository appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
        refreshConfig();
    }

    @Scheduled(fixedDelay = 3600000)
    public void refreshConfig() {
        log.debug("Refreshing app configs");
        appConfigRepository.findAll()
                .forEach(appConfig -> cachedAppConfigs.put(appConfig.getKey(), appConfig.getValue()));
    }

    public String getValOrDefault(String key, String defaultValue) {
        return cachedAppConfigs.getOrDefault(key, defaultValue);
    }

    public long getLongOrDefault(String key, long defaultValue) {
        try {
            return Long.parseLong(cachedAppConfigs.getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            log.warn("Invalid long value for key {}: {}", key, cachedAppConfigs.get(key));
            return defaultValue;
        }
    }

    public boolean getBooleanOrDefault(String key, boolean defaultValue) {
        String value = cachedAppConfigs.get(key);
        try {
            return value != null ? Boolean.parseBoolean(value) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid boolean value for key {}: {}", key, value);
            return defaultValue;
        }
    }
}
