package ru.olbreslavets.tgbank.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.constant.Templates;
import ru.olbreslavets.tgbank.dto.context.ContextDto;
import ru.olbreslavets.tgbank.service.ContextService;
import ru.olbreslavets.tgbank.service.PaymentService;
import ru.olbreslavets.tgbank.service.UserService;
import ru.olbreslavets.tgbank.telegram.TelegramBot;
import ru.olbreslavets.tgbank.util.DateUtil;
import ru.olbreslavets.tgbank.util.DocumentUtil;
import ru.olbreslavets.tgbank.util.KeyboardUtil;

import java.io.File;

@Service
@RequiredArgsConstructor
public class HistoryCommandHandler implements CommandHandler {

    private final PaymentService paymentService;
    private final UserService userService;
    private final ContextService contextService;

    @Override
    public String getCommand() {
        return Commands.HISTORY.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        var tgId = message.getFrom().getId();
        var user = userService.findByTgId(tgId).orElse(null);
        if (user == null) {
            bot.execute(sendMessage(chatId, Templates.ERROR_AUTH_TEMP.formatted(message.getFrom().getUserName())));
        } else {
            bot.execute(sendHistory(chatId, tgId));
        }
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context != null) {
            switch (Stage.valueOf(context.stage())) {
                case Stage.HISTORY -> bot.execute(handleHistory(chatId, callbackQuery));
            }
        }
    }

    private SendDocument handleHistory(String chatId, CallbackQuery callbackQuery) {
        var idPayment = Long.valueOf(callbackQuery.getData());
        var tgId = callbackQuery.getFrom().getId();
        var payment = paymentService.getLastFiveByUser(tgId).stream()
                .filter(p -> p.getId().equals(idPayment))
                .findFirst()
                .get();

        var name = "files/payment.pdf";
        var text = Templates.PAYMENT_PDF_TEMP.formatted(payment.getOperDate().format(DateUtil.dd_MM_yyyy_WITH_TIME), payment.getAmount(), payment.getPurpose(), payment.getFrom(), payment.getTo());
        DocumentUtil.generatePdf(name, text);
        var file = new InputFile(new File(name));

        return sendDocument(chatId, Templates.GENERATED_PAYMENT.formatted(idPayment.toString()), file);
    }

    private SendMessage sendHistory(String chatId, Long tgId) {
        StringBuilder builder = new StringBuilder();
        builder.append(Templates.HISTORY);
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        var payments = paymentService.getLastFiveByUser(tgId);
        for (int i = 0; i < payments.size(); i++) {
            var e = payments.get(i);
            var text = Templates.PAYMENT_TEMP.formatted(i + 1, e.getOperDate().format(DateUtil.dd_MM_yyyy_WITH_TIME), e.getAmount(), e.getPurpose(), e.getFrom(), e.getTo());
            builder.append(text);
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        builder.append("Для скачивания платежного поручения выберите операцию");

        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            context = new ContextDto(chatId, Commands.HISTORY, Stage.HISTORY.name(), "", "");
            contextService.create(context);
        }
        var keyboardMarkup = KeyboardUtil.getDynamicDigitInlineKeyboardPayment(payments);
        var message = sendMessage(chatId, builder.toString());
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }


    @Getter
    @AllArgsConstructor
    private enum Stage {
        HISTORY("История");

        private String description;
    }

}
