package school.faang.user_service.service.elastic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.elastic_search.UserDocument;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.elastic_search.UserDocumentRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserElasticService {

    private final UserDocumentRepository userDocumentRepository;
    private final UserMapper userMapper;

    public void bulkUsers(List<User> users) {
        List<UserDocument> documents = userMapper.toDocumentList(users);
        userDocumentRepository.saveAll(documents);
        log.info("Fetched all users from database and bulked them into Elastic Search");
    }

}
