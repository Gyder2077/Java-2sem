package main.Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилита для хэширования паролей алгоритмом MD5.
 * Возвращает хэш в виде строки из 32 шестнадцатеричных символов.
 */
public class PasswordHasher {

    private PasswordHasher() {
        // Приватный конструктор, чтобы нельзя было создать экземпляр
    }

    /**
     * Хэширует переданный пароль с помощью MD5.
     * @param password исходный пароль в виде строки
     * @return хэш пароля в формате hex (32 символа)
     */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(32);
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * Проверяет, совпадает ли пароль с хэшем.
     * @param password исходный пароль
     * @param storedHash хэш, который хранится в БД
     * @return true, если пароль верный
     */
    public static boolean verify(String password, String storedHash) {
        return hash(password).equals(storedHash);
    }
}