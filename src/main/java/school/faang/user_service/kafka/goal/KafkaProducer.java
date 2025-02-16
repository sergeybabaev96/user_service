package school.faang.user_service.kafka.goal;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaProducer<T> {

    void produce(T obj) throws JsonProcessingException;
}