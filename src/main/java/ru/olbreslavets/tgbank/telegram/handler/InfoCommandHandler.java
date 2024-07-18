package ru.olbreslavets.tgbank.telegram.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.constant.Templates;
import ru.olbreslavets.tgbank.service.BankService;
import ru.olbreslavets.tgbank.telegram.TelegramBot;

@Service
@RequiredArgsConstructor
public class InfoCommandHandler implements CommandHandler {

    private final BankService bankService;

    @Override
    public String getCommand() {
        return Commands.INFO.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        var bankInfo = bankService.getBankInfo();
        bot.execute(sendMessage(chatId, Templates.INFO_TEMP.formatted(bankInfo.bik(), bankInfo.name(), bankInfo.legalAddress(), bankInfo.contact().phone(), bankInfo.contact().email())));
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {

    }

}
