package school.faang.user_service.validation.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    public static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && ALLOWED_TYPES.contains(contentType);
    }
}
