package ru.olbreslavets.tgbank.telegram.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.constant.Templates;
import ru.olbreslavets.tgbank.telegram.TelegramBot;

@Component
public class UnnownCommandHandler implements CommandHandler {

    @Override
    public String getCommand() {
        return Commands.UNKNOWN.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        bot.execute(sendMessage(chatId, Templates.UNKNOWN));
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {

    }

}
