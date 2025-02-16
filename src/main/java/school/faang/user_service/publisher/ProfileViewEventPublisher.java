package school.faang.user_service.publisher;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEventDto;

@Component
public class ProfileViewEventPublisher extends KafkaEventPublisher<ProfileViewEventDto> {

    public ProfileViewEventPublisher(NewTopic topic1, KafkaTemplate<String, Object> kafkaTemplate) {
        super(topic1, kafkaTemplate);
    }

    @Override
    public void publish(ProfileViewEventDto message) {

    }
}
