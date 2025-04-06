package school.faang.user_service.service.avatar;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.service.custommultipartfile.CustomMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Сервис для изменения размера изображений.
 * <p>
 * Обеспечивает функционал масштабирования изображений с сохранением качества.
 * Использует библиотеку imgscalr для высококачественного ресайза изображений.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ImageResize {

    /**
     * Изменяет размер изображения, сохраняя пропорции.
     * <p>
     * Алгоритм работы:
     * 1. Читает исходное изображение из MultipartFile
     * 2. Масштабирует изображение до указанного размера (по наибольшей стороне)
     * 3. Сохраняет результат в формате JPG
     * 4. Возвращает как новый MultipartFile
     * </p>
     *
     * @param file исходный файл изображения
     * @param maxSize максимальный размер (в пикселях) для наибольшей стороны изображения
     * @return новый MultipartFile с измененным размером
     * @throws FileException если произошла ошибка чтения/записи изображения
     *
     * @apiNote Всегда сохраняет результат в формате JPG независимо от исходного формата
     * @implNote Использует алгоритм масштабирования Scalr.Method.QUALITY для наилучшего качества
     */
    public MultipartFile resizeImage(@NotNull MultipartFile file, int maxSize) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, maxSize);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            return new CustomMultipartFile(file.getOriginalFilename()
                    , outputStream.toByteArray()
                    , file.getContentType());
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }
    }
}
