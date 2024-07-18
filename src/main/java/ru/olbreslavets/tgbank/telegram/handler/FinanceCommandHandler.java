package ru.olbreslavets.tgbank.telegram.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.constant.Templates;
import ru.olbreslavets.tgbank.service.AccountService;
import ru.olbreslavets.tgbank.service.UserService;
import ru.olbreslavets.tgbank.telegram.TelegramBot;

@Service
@RequiredArgsConstructor
public class FinanceCommandHandler implements CommandHandler {

    private final AccountService accountService;
    private final UserService userService;

    @Override
    public String getCommand() {
        return Commands.FINANCE.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        var tgId = message.getFrom().getId();
        var user = userService.findByTgId(tgId).orElse(null);
        if (user == null) {
            bot.execute(sendMessage(chatId, Templates.ERROR_AUTH_TEMP.formatted(message.getFrom().getUserName())));
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(Templates.FINANCE);
            builder.append(System.lineSeparator());
            builder.append(System.lineSeparator());
            var accounts = accountService.getAll(tgId);
            accounts.forEach(a -> {
                var text = Templates.ACCOUNT_TEMP.formatted(a.number(), a.balance().toString(), a.isMain() ? " (для переводов СБП)" : "");
                builder.append(text);
                builder.append(System.lineSeparator());
            });
            bot.execute(sendMessage(chatId, builder.toString()));
        }
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {

    }

}
