package school.faang.user_service.filter;

import school.faang.user_service.entity.User;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UserFilter implements Predicate<User> {
    private int experienceMin;
    private int experienceMax;
    private boolean experienceCheck = false;
    private final Pattern namePattern;
    private final Pattern phonePattern;

    public UserFilter(String nameRegex, String phoneRegex) {
        this.namePattern = nameRegex == null || nameRegex.isBlank() ? null : Pattern.compile(nameRegex);
        this.phonePattern = phoneRegex == null || phoneRegex.isBlank() ? null : Pattern.compile(phoneRegex);
    }

    public UserFilter(int experienceMin, int experienceMax) {
        this(null, null, experienceMin, experienceMax);
    }

    public UserFilter(String nameRegex, String phoneRegex, int experienceMin, int experienceMax) {
        this(nameRegex, phoneRegex);
        this.experienceCheck = true;
        this.experienceMin = experienceMin;
        this.experienceMax = experienceMax;
    }

    @Override
    public boolean test(User user) {
        if (user == null) return false;

        boolean matchesName = (namePattern == null) || namePattern.matcher(user.getUsername()).matches();
        boolean matchesPhone = (phonePattern == null) || phonePattern.matcher(user.getPhone()).matches();
        boolean experienceInRange = !experienceCheck ||
                (user.getExperience() >= experienceMin && user.getExperience() <= experienceMax);

        return matchesName && matchesPhone && experienceInRange;
    }
}
