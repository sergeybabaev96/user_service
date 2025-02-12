package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.file.FileUploadResponseDto;

public interface UserService {
    FileUploadResponseDto processPersonsFromFile(MultipartFile file);
}
