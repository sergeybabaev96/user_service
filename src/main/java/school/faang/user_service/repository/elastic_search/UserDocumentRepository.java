package school.faang.user_service.repository.elastic_search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import school.faang.user_service.dto.elastic_search.UserDocument;

public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, Long> {

}
