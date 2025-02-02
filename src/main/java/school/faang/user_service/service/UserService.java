package school.faang.user_service.service;

import school.faang.user_service.dto.file.FileUploadResponseDto;

import java.io.InputStream;

public interface UserService {
    FileUploadResponseDto parseCsv(InputStream fileInputStream);
}
