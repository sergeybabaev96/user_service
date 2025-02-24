package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaProducer<T> {

    void produce(T obj) throws JsonProcessingException;
}