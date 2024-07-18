package ru.olbreslavets.tgbank.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class PhoneUtil {

    public static boolean isRussianPhoneNumber(String phoneNumber) {
        // Убираем все символы, кроме цифр
        String cleanedNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Определяем регулярное выражение для российских телефонных номеров (должно быть ровно 11 цифр)
        String pattern = "^(?:8|7)\\d{10}$";

        // Проверяем номер на соответствие паттерну
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(cleanedNumber);
        return matcher.matches();
    }

    public static String formatRussianNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }

        if (phoneNumber.startsWith("+7")) {
            return phoneNumber.substring(2);
        } else if (phoneNumber.startsWith("8")) {
            return phoneNumber.substring(1);
        } else if (phoneNumber.startsWith("7")) {
            return phoneNumber.substring(1);
        } else {
            return phoneNumber;
        }
    }

}
