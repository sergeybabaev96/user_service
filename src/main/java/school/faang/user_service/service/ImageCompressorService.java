package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.constants.goal.ImageConstants;
import school.faang.user_service.dto.custom.CustomMultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;

@Service
@Slf4j
public class ImageCompressorService {

    public MultipartFile compressImage(MultipartFile file, int size) {
        try {
            log.debug("Start to compress image");
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            if (originalImage == null) {
                throw new IOException("Can't read image");
            }
            int[] dimensions = calculateNewSize(originalImage.getWidth(), originalImage.getHeight(), size);

            if (dimensions[0] == originalImage.getWidth()) {
                return file;
            }
            BufferedImage resizedImage = resizeImage(originalImage, dimensions[0], dimensions[1]);

            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                ImageIO.write(resizedImage, getFormatName(file.getContentType()), output);
                return CustomMultipartFile.builder()
                        .content(output.toByteArray())
                        .name(file.getName())
                        .originalFilename(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .build();
            }
        } catch (IOException ex) {
            log.error("Image compression failed. {}", ex.toString());
            throw new RuntimeException(ex.getMessage());
        }
    }

    private int[] calculateNewSize(int width, int height, int maxSize) {
        log.debug("Calculating new size of image");
        boolean isLandscape = width > height;

        if (isLandscape) {
            int newHeight = (int) ((double) height / width * maxSize);
            return new int[]{maxSize, newHeight};
        } else {
            int newWidth = (int) ((double) width / height * maxSize);
            return new int[]{newWidth, maxSize};
        }
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, original.getType());
        Graphics2D graphics = resized.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(original, 0, 0, width, height, null);
        graphics.dispose();
        log.debug("Image resize completed");
        return resized;
    }

    private String getFormatName(String contentType) {
        if (contentType == null) {
            return ImageConstants.DEFAULT_FORMAT;
        }
        Matcher matcher = ImageConstants.FORMAT_EXTRACTOR.matcher(contentType);
        return matcher.find() ? matcher.group(1) : ImageConstants.DEFAULT_FORMAT;
    }
}
