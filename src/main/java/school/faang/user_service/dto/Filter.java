package school.faang.user_service.dto;

import java.util.List;

public interface Filter<E, F> {

    boolean isApplicable(F dto);

    List<E> apply(List<E> users, F filters);

}