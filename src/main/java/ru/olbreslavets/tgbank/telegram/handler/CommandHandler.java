package ru.olbreslavets.tgbank.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.telegram.TelegramBot;

public interface CommandHandler {

    String getCommand();

    void handleMessage(Message message, TelegramBot bot) throws TelegramApiException;

    void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException;

    default SendMessage sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        return sendMessage;
    }

    default SendDocument sendDocument(String chatId, String text, InputFile file) {
        SendDocument document = new SendDocument();
        document.setChatId(chatId);
        document.setDocument(file);
        document.setCaption(text);

        return document;
    }

}
