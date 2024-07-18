package ru.olbreslavets.tgbank.telegram.handler;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.telegram.TelegramBot;

import java.io.File;

@Service
public class HelpCommandHandler implements CommandHandler {

    private static final String HELP = """
            HELP
            HELP
            HELP
            AAAAAAAAA
            """;

    @Override
    public String getCommand() {
        return Commands.HELP.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        var file = new InputFile(new File("files/manual.pdf"));
        bot.execute(sendDocument(chatId, "Скачайте руководство пользователя", file));
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {

    }


}
