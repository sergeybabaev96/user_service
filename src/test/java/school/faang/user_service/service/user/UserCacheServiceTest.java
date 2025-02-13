package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.config.async.AsyncConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.user_cache.UserCacheDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.publisher.kafka.KafkaHeatFeedCacheProducer;
import school.faang.user_service.repository.cache.UserCacheRepository;
import school.faang.user_service.service.avatar.AvatarService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCacheServiceTest {

    @Mock
    private UserCacheRepository userCacheRepository;

    @Mock
    private AvatarService avatarService;

    @Mock
    private AsyncConfig asyncConfig;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Mock
    private UserService userService;

    @Mock
    private KafkaHeatFeedCacheProducer kafkaHeatFeedCacheProducer;

    @InjectMocks
    private UserCacheService userCacheService;

    @Value("${application.kafka.heat-feed-batch-size}")
    private int pageSize = 10;

    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        setPageSize(userCacheService, 10);

    }

    @Test
    void testStartHeatFeedCacheWithEmptyPageTest() {
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, pageSize), 0);

        when(userService.findActiveUsersIds(PageRequest.of(0, pageSize))).thenReturn(emptyPage);

        userCacheService.startHeatFeedCache();

        verify(kafkaHeatFeedCacheProducer, never()).send(anyList());
        verifyNoMoreInteractions(kafkaHeatFeedCacheProducer);
    }

    @Test
    void testStartHeatFeedCacheWithMultiplePagesTest() {
        List<Long> userIdsPage1 = Arrays.asList(1L, 2L, 3L);
        List<Long> userIdsPage2 = Arrays.asList(4L, 5L);

        Page<Long> page1 = Mockito.mock(Page.class);
        Page<Long> page2 = Mockito.mock(Page.class);

        when(page1.getContent()).thenReturn(userIdsPage1);
        when(page2.getContent()).thenReturn(userIdsPage2);

        when(page1.hasContent()).thenReturn(true);
        when(page2.hasContent()).thenReturn(true);

        when(page1.isLast()).thenReturn(false);
        when(page2.isLast()).thenReturn(true);

        when(userService.findActiveUsersIds(PageRequest.of(0, pageSize))).thenReturn(page1);
        when(userService.findActiveUsersIds(PageRequest.of(1, pageSize))).thenReturn(page2);

        userCacheService.startHeatFeedCache();

        verify(kafkaHeatFeedCacheProducer).send(userIdsPage1);
        verify(kafkaHeatFeedCacheProducer).send(userIdsPage2);
    }

    @Test
    void getUsersCachesDtosTest() throws InterruptedException {
        List<Long> usersIds = new ArrayList<>(List.of(1L, 2L));
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();
        byte[] pictureData = new byte[10];
        List<User> users = new ArrayList<>(List.of(firstUser, secondUser));

        when(userService.findAllUsersByIds(usersIds)).thenReturn(users);
        when(avatarService.getUserAvatar(any())).thenReturn(pictureData);
        when(asyncConfig.taskExecutor()).thenReturn(taskExecutor);

        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            latch.countDown();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        List<UserCacheDto> result = userCacheService.getUsersCachesDtos(usersIds);

        latch.await();

        verify(userCacheRepository).saveBatchUsersToCache(anyList());
        verify(userMapper).toListUserCacheDto(users);
        verify(userService).findAllUsersByIds(usersIds);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void saveUsersToCacheTest() {
        List<Long> usersIds = new ArrayList<>(List.of(1L, 2L));
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();
        byte[] pictureData = new byte[10];
        List<User> users = new ArrayList<>(List.of(firstUser, secondUser));

        when(userService.findAllUsersByIds(usersIds)).thenReturn(users);
        when(avatarService.getUserAvatar(any())).thenReturn(pictureData);

        userCacheService.saveUsersToCache(usersIds);

        verify(userService).findAllUsersByIds(usersIds);
        verify(userMapper, times(2)).toUserCacheDto(any());
        verify(avatarService, times(2)).getUserAvatar(any());
        verify(userCacheRepository).saveBatchUsersToCache(anyList());
    }

    private void setPageSize(Object targetObject, int value) throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField("pageSize");
        field.setAccessible(true);
        field.set(targetObject, value);
    }
}