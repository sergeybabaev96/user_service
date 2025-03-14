package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Set;

@Component
public class ResourseValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> SUPPORTED_FORMATS = Set.of("png", "jpg", "jpeg", "webp");

    public void validate(MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed!");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 5MB!");
        }
        if (getFileExtension(file).isEmpty()) {
            throw new IllegalArgumentException("File must have an extension!");
        }
        if (!SUPPORTED_FORMATS.contains(getFileExtension(file))) {
            throw new IllegalArgumentException("Unsupported file format! Allowed: " + SUPPORTED_FORMATS);
        }
    }

    public String getFileExtension(MultipartFile file) {
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }
}