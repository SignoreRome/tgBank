package ru.olbreslavets.tgbank.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olbreslavets.tgbank.dto.AccountDto;
import ru.olbreslavets.tgbank.dto.PaymentDto;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class KeyboardUtil {

    public static InlineKeyboardMarkup getDynamicDigitInlineKeyboard(List<AccountDto> list) {
        int line = 5;
        int rows = (list.size() + line - 1) / line;
        int count = 0;

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(rows);

        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> rowButtons = new ArrayList<>(line);
            for (int j = 0; j < line; j++) {
                if (count == list.size()) {
                    break;
                }
                var button = new InlineKeyboardButton();
                button.setText(String.valueOf(i + j + 1));
                button.setCallbackData(String.valueOf(list.get(count).number()));
                rowButtons.add(button);
                count++;
            }
            keyboard.add(rowButtons);
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getDynamicDigitInlineKeyboardPayment(List<PaymentDto> list) {
        int line = 5;
        int rows = (list.size() + line - 1) / line;
        int count = 0;

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(rows);

        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> rowButtons = new ArrayList<>(line);
            for (int j = 0; j < line; j++) {
                if (count == list.size()) {
                    break;
                }
                var button = new InlineKeyboardButton();
                button.setText(String.valueOf(i + j + 1));
                button.setCallbackData(String.valueOf(list.get(count).getId()));
                rowButtons.add(button);
                count++;
            }
            keyboard.add(rowButtons);
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

}
