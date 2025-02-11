package school.faang.user_service.filter;

import java.util.List;

public interface Filter<E, F> {

    boolean isApplicable(F dto);

    List<E> apply(List<E> collection, F filters);

}