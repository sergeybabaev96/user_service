package school.faang.user_service.filter;

import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface Filter<T, F> {

    boolean isApplicable(F filter);

    void apply(Stream<T> element, F filter);
}
