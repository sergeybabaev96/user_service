package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;

import java.security.SecureRandom;

@Service
public class PasswordService {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + DIGITS + SPECIAL;

    private final SecureRandom random = new SecureRandom();

    public String generateRandomPassword(int length) {
        if (length < 10) {
            throw new DataValidationException("Пароль должен быть минимум 10 символов");
        }

        StringBuilder password = new StringBuilder(length);
        password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        return shuffleString(password.toString());
    }

    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

}
