package school.faang.user_service.service.custommultipartfile;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Реализация интерфейса {@link MultipartFile} для работы с файлами в памяти.
 * <p>
 * Класс предоставляет обертку вокруг байтового массива, позволяющую использовать его
 * как стандартный {@link MultipartFile} в Spring-приложениях.
 * </p>
 *
 * <p><b>Основные характеристики:</b></p>
 * <ul>
 *   <li>Хранение файловых данных в памяти (byte[])</li>
 *   <li>Поддержка всех операций стандартного MultipartFile</li>
 *   <li>Потокобезопасное чтение данных</li>
 *   <li>Возможность сохранения на диск</li>
 * </ul>
 *
 */
@RequiredArgsConstructor
public class CustomMultipartFile implements MultipartFile {
    private final String fileName;
    private final byte[] content;
    private final String contentType;

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}
