package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO-класс для представления информации о существующем пользователе.
 * <p>
 * Используется для передачи данных о пользователе между слоями приложения.
 * </p>
 * <p>
 * Содержит следующие поля:
 * <ul>
 *     <li>{@link #id Идентификатор пользователя}</li>
 *     <li>{@link #username Имя пользователя}</li>
 *     <li>{@link #menteesIds Идентификаторы менти,
 *     которые прикреплены к пользователю}</li>
 *     <li>{@link #mentorsIds Идентификаторы менторов,
 *     которые прикреплены к пользователю}</li>
 * </ul>
 * </p>
 *
 * @author gulnaz21
 */
@Data
public class UserViewDto {
    private Long id;
    private String username;
    private String email;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
}
