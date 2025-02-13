package school.faang.user_service.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.user_cache.UserCacheDto;
import school.faang.user_service.properties.RedisCacheProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisCacheProperties prop;

    public void saveBatchUsersToCache(List<UserCacheDto> userCacheDtos) {
        Map<String, Object> cacheMap = userCacheDtos.stream()
                .collect(Collectors.toMap(
                        userCacheDto -> prop.getUsersCacheName() + userCacheDto.getUserId(),
                        userCacheDto -> userCacheDto
                ));
        redisTemplate.opsForValue().multiSet(cacheMap);
    }
}
