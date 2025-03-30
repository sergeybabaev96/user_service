package school.faang.user_service.s3;

import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.AbstractResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Реализация {@link org.springframework.core.io.Resource} для работы с объектами Amazon S3.
 * <p>
 * Позволяет использовать объекты из S3 как обычные ресурсы в Spring-приложениях,
 * автоматически управляя жизненным циклом соединений и потоков данных.
 * </p>
 *
 * <p><b>Основные особенности:</b></p>
 * <ul>
 *   <li>Автоматическое закрытие S3-соединения при завершении работы с ресурсом</li>
 *   <li>Поддержка стандартных операций чтения данных</li>
 *   <li>Предоставление метаинформации о файле (имя, размер)</li>
 *   <li>Интеграция с механизмами Spring для работы с ресурсами</li>
 * </ul>
 *
 */
@RequiredArgsConstructor
public class S3Resource extends AbstractResource implements AutoCloseable {
    private final S3Object s3Object;
    private final String fileName;

    /**
     * Возвращает описание ресурса для логгирования и диагностики.
     *
     * @return Строка описания ресурса в формате "S3 resource [bucket/key]"
     */
    @Override
    public String getDescription() {
        return "S3 resource [" + s3Object.getBucketName() + "/" + s3Object.getKey() + "]";
    }

    /**
     * Открывает поток для чтения данных из S3-объекта.
     *
     * @return InputStream для чтения содержимого файла
     * @throws IOException если произошла ошибка при открытии потока
     * @throws IllegalStateException если S3-объект уже был закрыт
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return s3Object.getObjectContent();
    }

    /**
     * Возвращает имя файла, ассоциированное с ресурсом.
     *
     * @return Имя файла, переданное в конструктор
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Возвращает размер содержимого S3-объекта в байтах.
     *
     * @return Размер файла в байтах
     * @throws IOException если произошла ошибка при получении метаданных
     */
    @Override
    public long contentLength() throws IOException {
        return s3Object.getObjectMetadata().getContentLength();
    }

    /**
     * Закрывает ресурс и освобождает все связанные с ним системные ресурсы.
     * <p>
     * Последовательность закрытия:
     * <ol>
     *   <li>Закрывается поток данных (InputStream)</li>
     *   <li>Закрывается соединение с S3 (S3Object)</li>
     * </ol>
     * </p>
     *
     */
    @Override
    public void close() throws IOException {
        try {
            if (getInputStream() != null) {
                getInputStream().close();
            }
        } finally {
            if (s3Object != null) {
                s3Object.close();
            }
        }
    }
}
