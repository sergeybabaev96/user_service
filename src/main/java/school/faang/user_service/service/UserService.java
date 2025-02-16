package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void processPersonsFromFile(MultipartFile file);
}
