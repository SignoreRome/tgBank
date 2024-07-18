package ru.olbreslavets.tgbank.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.constant.Templates;
import ru.olbreslavets.tgbank.dto.context.ContextDto;
import ru.olbreslavets.tgbank.service.ContextService;
import ru.olbreslavets.tgbank.service.UserService;
import ru.olbreslavets.tgbank.telegram.TelegramBot;
import ru.olbreslavets.tgbank.util.PhoneUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCommandHandler implements CommandHandler {

    private final ContextService contextService;
    private final UserService userService;

    @Override
    public String getCommand() {
        return Commands.START.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        var userName = message.getFrom().getUserName();
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            bot.execute(sendMessage(chatId, Templates.GREETING_TEMP.formatted(userName)));
            bot.execute(sendPhoneNumberRequest(chatId));
            context = new ContextDto(chatId, Commands.START, Stage.INIT.name(), message.getText(), "ASK_CONTACT");
            contextService.create(context);
        } else {
            switch (Stage.valueOf(context.stage())) {
                case Stage.INIT -> bot.execute(handleContactMessage(message));
            }
        }
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {

    }

    private SendMessage handleContactMessage(Message message) {
        var phone = PhoneUtil.formatRussianNumber(message.getContact().getPhoneNumber());
        var chatId = String.valueOf(message.getChatId());
        var tgId = message.getFrom().getId();

        if (!message.getContact().getUserId().equals(message.getFrom().getId())) {
            return sendMessage(chatId, "Вы отправили не свой номер телефона");
        }

        try {
            var user = userService.updateTgId(phone, tgId);
            contextService.clear(chatId);
            return sendMessage(chatId, "%s, добро пожаловать!".formatted(user.fio()));
        } catch (RuntimeException e) {
            return sendMessage(chatId, Templates.USER_NOT_FOUND);
        }
    }

    private SendMessage sendPhoneNumberRequest(String chatId) {
        var message = sendMessage(chatId, Templates.AUTH_PHONE);

        // Создание кнопки для запроса номера телефона
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);  // Настройка размера клавиатуры
        keyboardMarkup.setOneTimeKeyboard(true); // Клавиатура исчезнет после использования

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("Отправить номер телефона");
        button.setRequestContact(true); // Запрос номера телефона
        row.add(button);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    @Getter
    @AllArgsConstructor
    private enum Stage {
        INIT("Начальная");

        private String description;
    }

}
