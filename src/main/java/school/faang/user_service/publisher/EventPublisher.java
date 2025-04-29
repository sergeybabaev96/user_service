package school.faang.user_service.publisher;

import school.faang.user_service.dto.EventMessage;


public interface EventPublisher {

    void publish(EventMessage message);

}
