package ru.olbreslavets.tgbank.telegram.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.constant.Templates;
import ru.olbreslavets.tgbank.service.UserService;
import ru.olbreslavets.tgbank.telegram.TelegramBot;
import ru.olbreslavets.tgbank.util.DateUtil;

@Service
@RequiredArgsConstructor
public class DataCommandHandler implements CommandHandler {

    private final UserService userService;

    @Override
    public String getCommand() {
        return Commands.DATA.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        var tgId = message.getFrom().getId();
        var user = userService.findByTgId(tgId).orElse(null);
        if (user == null) {
            bot.execute(sendMessage(chatId, Templates.ERROR_AUTH_TEMP.formatted(message.getFrom().getUserName())));
        } else {
            bot.execute(sendMessage(chatId, Templates.USER_TEMP.formatted(user.fio(), user.birthDate().format(DateUtil.dd_MM_yyyy), user.phone())));
        }
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {

    }


}
