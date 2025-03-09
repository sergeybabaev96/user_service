package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Service
public class MentorshipRequestFilter {
    public List<MentorshipRequest> filterRequests(List<MentorshipRequest> requests, RequestFilterDto filterRequest) {
        return requests.stream()
                .filter(reg ->
                        filterRequest.getDescription() == null || reg.getDescription()
                                .contains(filterRequest.getDescription()))
                .filter(reg ->
                        filterRequest.getReceiverId() == null || reg.getReceiver().getId()
                                .equals(filterRequest.getReceiverId()))
                .filter(reg ->
                        filterRequest.getRequesterId() == null || reg.getRequester().getId()
                                .equals(filterRequest.getRequesterId()))
                .filter(reg ->
                        filterRequest.getStatus() == null || reg.getStatus().equals(filterRequest.getStatus()))
                .toList();
    }
}
