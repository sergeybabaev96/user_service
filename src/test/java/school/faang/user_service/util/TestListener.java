package school.faang.user_service.util;

import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestListener extends JedisPubSub {
    private String channel;
    private String message;
    private final CountDownLatch subscribeLatch = new CountDownLatch(1);

    @Override
    public void onMessage(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        subscribeLatch.countDown();
    }

    public void awaitSubscription() {
        try {
            subscribeLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }
}
