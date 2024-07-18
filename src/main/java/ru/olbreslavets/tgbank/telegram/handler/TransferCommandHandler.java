package ru.olbreslavets.tgbank.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.constant.Templates;
import ru.olbreslavets.tgbank.dto.AccountDto;
import ru.olbreslavets.tgbank.dto.context.ContextDto;
import ru.olbreslavets.tgbank.dto.context.ContextUpdateDto;
import ru.olbreslavets.tgbank.service.AccountService;
import ru.olbreslavets.tgbank.service.ContextService;
import ru.olbreslavets.tgbank.service.TransferService;
import ru.olbreslavets.tgbank.service.UserService;
import ru.olbreslavets.tgbank.telegram.TelegramBot;
import ru.olbreslavets.tgbank.util.AccountUtil;
import ru.olbreslavets.tgbank.util.KeyboardUtil;
import ru.olbreslavets.tgbank.util.PhoneUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferCommandHandler implements CommandHandler {

    private final ContextService contextService;
    private final UserService userService;
    private final AccountService accountService;
    private final TransferService transferService;

    @Override
    public String getCommand() {
        return Commands.TRANSFER.name();
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(message.getChatId());
        var tgId = message.getFrom().getId();
        var user = userService.findByTgId(tgId).orElse(null);
        if (user == null) {
            bot.execute(sendMessage(chatId, Templates.ERROR_AUTH_TEMP.formatted(message.getFrom().getUserName())));
        } else if (transferService.isAvailable(tgId)) {
            var context = contextService.findByChatId(chatId).orElse(null);
            if (context == null) {
                bot.execute(sendChoiceTransfer(chatId));
                context = new ContextDto(chatId, Commands.TRANSFER, Stage.TRANSFER.name(), message.getText(), "CHOICE_TRANSFER");
                contextService.create(context);
            } else {
                switch (Stage.valueOf(context.stage())) {
                    case Stage.TO_SELF_TO -> bot.execute(handleSelfTransferTo(chatId, message.getText()));
                    case Stage.TO_USER_TO_ACC -> bot.execute(handleUserTransferToAcc(chatId, message.getText()));
                    case Stage.TO_USER_TO_PHONE -> bot.execute(handleUserTransferToPhone(chatId, message.getText()));
                    case Stage.TO_USER_AMOUNT -> bot.execute(handleUserTransferAmount(chatId, message.getText()));
                }
            }
        } else {
            bot.execute(sendMessage(chatId, Templates.NO_TRANSFER));
        }
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery, TelegramBot bot) throws TelegramApiException {
        var chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context != null) {
            switch (Stage.valueOf(context.stage())) {
                case Stage.TRANSFER -> bot.execute(handleTransfer(chatId, callbackQuery));
                case Stage.TO_SELF -> bot.execute(handleSelfTransfer(chatId, callbackQuery));
                case Stage.TO_SELF_FROM -> bot.execute(handleSelfTransferFrom(chatId, callbackQuery));
                case Stage.TO_SELF_AMOUNT -> bot.execute(handleTransferAmount(chatId, callbackQuery, "Перевод между счетами"));
                case Stage.TO_USER -> bot.execute(handleUserTransfer(chatId, callbackQuery));
                case Stage.TO_USER_FROM -> bot.execute(handleUserTransferFrom(chatId, callbackQuery));
                case Stage.TO_USER_CONFIRM -> bot.execute(handleTransferAmount(chatId, callbackQuery, "Перевод по номеру"));
            }
        }
    }

    //Метод для обработки этапа TRANSFER
    private SendMessage handleTransfer(String chatId, CallbackQuery callbackQuery) {
        return switch (TransferButton.valueOf(callbackQuery.getData())) {
            case TransferButton.TO_SELF -> {
                var contextUpd = new ContextUpdateDto(Stage.TO_SELF.name(), TransferButton.TO_SELF.name(), "");
                contextService.update(chatId, contextUpd);

                yield sendChoiceTransferFrom(chatId, callbackQuery);
            }
            case TransferButton.TO_USER -> {
                var contextUpd = new ContextUpdateDto(Stage.TO_USER.name(), TransferButton.TO_USER.name(), Templates.ENTER_PHONE);
                contextService.update(chatId, contextUpd);

                yield sendChoiceTransferFrom(chatId, callbackQuery);
            }
        };
    }

    //Метод для обработки этапа TO_USER_FROM
    private SendMessage handleUserTransferFrom(String chatId, CallbackQuery callbackQuery) {
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            return sendMessage(chatId, Templates.ERROR);
        }
        var numFrom = context.messageIn();


        return switch (UserTransferButton.valueOf(callbackQuery.getData())) {
            case UserTransferButton.ACCOUNT -> {
                var contextUpd = new ContextUpdateDto(Stage.TO_USER_TO_ACC.name(), "ACCOUNT", numFrom);
                contextService.update(chatId, contextUpd);

                yield sendMessage(chatId, "Введите номер счета получателя");
            }
            case UserTransferButton.PHONE -> {
                var contextUpd = new ContextUpdateDto(Stage.TO_USER_TO_PHONE.name(), "PHONE", numFrom);
                contextService.update(chatId, contextUpd);

                yield sendMessage(chatId, Templates.ENTER_PHONE);
            }
        };
    }

    //Метод для обработки этапа TO_USER
    private SendMessage handleUserTransfer(String chatId, CallbackQuery callbackQuery) {
        var context = new ContextUpdateDto(Stage.TO_USER_FROM.name(), callbackQuery.getData(), "");
        contextService.update(chatId, context);

        return sendChoiceUserTransfer(chatId);
    }

    //Метод для обработки этапа TO_SELF
    private SendMessage handleSelfTransfer(String chatId, CallbackQuery callbackQuery) {
        var tgId = callbackQuery.getFrom().getId();
        var accounts = new ArrayList<>(accountService.getAll(tgId));
        var pickNum = Long.valueOf(callbackQuery.getData());
        var accFrom = accounts.stream().filter(a -> a.number().equals(pickNum)).findFirst().orElse(null);

        var context = new ContextUpdateDto(Stage.TO_SELF_FROM.name(), callbackQuery.getData(), "");
        contextService.update(chatId, context);
        accounts.remove(accFrom);

        return sendChoiceSelfTransferTo(chatId, accounts);
    }

    //Метод для обработки этапа TO_SELF_FROM
    private SendMessage handleSelfTransferFrom(String chatId, CallbackQuery callbackQuery) {
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            return sendMessage(chatId, Templates.ERROR);
        }

        var numFrom = context.messageIn();
        var numTo = callbackQuery.getData();

        var contextUpd = new ContextUpdateDto(Stage.TO_SELF_TO.name(), numTo, numFrom);
        contextService.update(chatId, contextUpd);

        return sendMessage(chatId, "Введите сумму перевода:");
    }

    //Метод для обработки этапа TO_SELF_TO
    private SendMessage handleSelfTransferTo(String chatId, String amount) {
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            return sendMessage(chatId, Templates.ERROR);
        }

        var numFrom = context.messageOut();
        var numTo = context.messageIn();
        var out = "%s;%s".formatted(numFrom, numTo);

        try {
            new BigDecimal(amount);
        } catch (NumberFormatException e) {
            return sendMessage(chatId, "Введите корректное значение формата XXX.XX");
        }

        var contextUpd = new ContextUpdateDto(Stage.TO_SELF_AMOUNT.name(), amount, out);
        contextService.update(chatId, contextUpd);

        var text = Templates.SELF_TRANSFER_CONFIRM.formatted(numFrom, numTo, amount);

        return sendConfirmOperation(chatId, text);
    }

    //Метод для обработки этапа TO_SELF_AMOUNT
    private SendMessage handleTransferAmount(String chatId, CallbackQuery callbackQuery, String purpose) {
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            return sendMessage(chatId, Templates.ERROR);
        }
        return switch (ConfirmButton.valueOf(callbackQuery.getData())) {
            case ConfirmButton.YES -> {
                var nums = context.messageOut().split(";");
                var numFrom = Long.valueOf(nums[0]);
                var numTo = Long.valueOf(nums[1]);
                var amount = new BigDecimal(context.messageIn());
                try {
                    transferService.transferByNums(numFrom, numTo, amount, purpose);
                } catch (IllegalArgumentException e) {
                    yield sendMessage(chatId, e.getMessage());
                }
                contextService.clear(chatId);
                yield sendMessage(chatId, Templates.SUCCEED);
            }
            case ConfirmButton.NO -> {
                contextService.clear(chatId);
                yield sendMessage(chatId, Templates.CANCELED);
            }
        };
    }


    //Метод для обработки этапа TO_USER_TO_ACC
    private SendMessage handleUserTransferToAcc(String chatId, String num) {
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            return sendMessage(chatId, Templates.ERROR);
        }
        var numFrom = context.messageOut();

        Long numTo;
        try {
            numTo = Long.valueOf(num);
        } catch (NumberFormatException e) {
            return sendMessage(chatId, "Введите корректный номер аккаунта");
        }

        if (!accountService.isExistByNumber(numTo)) {
            return sendMessage(chatId, "Такого счета не существует");
        }

        var contextUpd = new ContextUpdateDto(Stage.TO_USER_AMOUNT.name(), num, numFrom);
        contextService.update(chatId, contextUpd);

        return sendMessage(chatId, Templates.ENTER_TRANSFER_AMOUNT);
    }

    //Метод для обработки этапа TO_USER_TO_PHONE
    private SendMessage handleUserTransferToPhone(String chatId, String phone) {
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            return sendMessage(chatId, Templates.ERROR);
        }

        var numFrom = context.messageOut();
        if (!PhoneUtil.isRussianPhoneNumber(phone)) {
            return sendMessage(chatId, "Вы ввели номер не в верном формате");
        }

        phone = PhoneUtil.formatRussianNumber(phone);
        var user = userService.findByPhone(phone).orElse(null);
        if (user == null) {
            return sendMessage(chatId, "Пользователь не найден в системе");
        }

        var account = accountService.findMainByUserPhone(user.phone()).orElse(null);
        if (account == null) {
            return sendMessage(chatId, "У данного пользователя нет счета в банке");
        }

        var contextUpd = new ContextUpdateDto(Stage.TO_USER_AMOUNT.name(), String.valueOf(account.number()), numFrom);
        contextService.update(chatId, contextUpd);

        return sendMessage(chatId, Templates.ENTER_TRANSFER_AMOUNT);
    }

    //Метод для обработки этапа TO_USER_AMOUNT
    private SendMessage handleUserTransferAmount(String chatId, String amount) {
        var context = contextService.findByChatId(chatId).orElse(null);
        if (context == null) {
            return sendMessage(chatId, Templates.ERROR);
        }

        var numFrom = context.messageOut();
        var numTo = context.messageIn();
        var out = "%s;%s".formatted(numFrom, numTo);

        try {
            new BigDecimal(amount);
        } catch (NumberFormatException e) {
            return sendMessage(chatId, Templates.ENTER_CORRECT_AMOUNT);
        }

        var contextUpd = new ContextUpdateDto(Stage.TO_USER_CONFIRM.name(), amount, out);
        contextService.update(chatId, contextUpd);

        var user = userService.findByAccountNumber(Long.valueOf(numTo)).orElse(null);
        if (user == null) {
            return sendMessage(chatId, Templates.ERROR);
        }

        var text = Templates.USER_TRANSFER_CONFIRM.formatted(numFrom, user.phone() + " " + user.fio(), amount);

        return sendConfirmOperation(chatId, text);
    }

    //Метод для выбора счета получения
    private SendMessage sendChoiceSelfTransferTo(String chatId, List<AccountDto> accounts) {
        var keyboardMarkup = KeyboardUtil.getDynamicDigitInlineKeyboard(accounts);
        var message = sendMessage(chatId, AccountUtil.getAccountsText("Выберите счет получения:", accounts));
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    //Метод для выбора счета отправления
    private SendMessage sendChoiceTransferFrom(String chatId, CallbackQuery callbackQuery) {
        var tgId = callbackQuery.getFrom().getId();
        var accounts = accountService.getAll(tgId);

        var keyboardMarkup = KeyboardUtil.getDynamicDigitInlineKeyboard(accounts);
        var message = sendMessage(chatId, AccountUtil.getAccountsText("Выберите счет отправления:", accounts));
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    //Метод для выбора типа перевода
    private SendMessage sendChoiceTransfer(String chatId) {
        var message = sendMessage(chatId, "Какой перевод");

        var toSelfButton = new InlineKeyboardButton();
        toSelfButton.setText(TransferButton.TO_SELF.description);
        toSelfButton.setCallbackData(TransferButton.TO_SELF.name());

        var toUserButton = new InlineKeyboardButton();
        toUserButton.setText(TransferButton.TO_USER.description);
        toUserButton.setCallbackData(TransferButton.TO_USER.name());

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowButtons = List.of(toSelfButton, toUserButton);

        keyboardMarkup.setKeyboard(List.of(rowButtons));
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    //Метод для подтверждения перевода
    private SendMessage sendConfirmOperation(String chatId, String text) {
        var message = sendMessage(chatId, text);

        var yesButton = new InlineKeyboardButton();
        yesButton.setText(ConfirmButton.YES.description);
        yesButton.setCallbackData(ConfirmButton.YES.name());

        var noButton = new InlineKeyboardButton();
        noButton.setText(ConfirmButton.NO.description);
        noButton.setCallbackData(ConfirmButton.NO.name());

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowButtons = List.of(yesButton, noButton);

        keyboardMarkup.setKeyboard(List.of(rowButtons));
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    //Метод для выбора типа перевода пользователю
    private SendMessage sendChoiceUserTransfer(String chatId) {
        var message = sendMessage(chatId, "Какой перевод");

        var phoneButton = new InlineKeyboardButton();
        phoneButton.setText(UserTransferButton.PHONE.description);
        phoneButton.setCallbackData(UserTransferButton.PHONE.name());

        var accountButton = new InlineKeyboardButton();
        accountButton.setText(UserTransferButton.ACCOUNT.description);
        accountButton.setCallbackData(UserTransferButton.ACCOUNT.name());

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowButtons = List.of(phoneButton, accountButton);

        keyboardMarkup.setKeyboard(List.of(rowButtons));
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    @Getter
    @AllArgsConstructor
    private enum ConfirmButton {
        YES("ДА"),
        NO("НЕТ");

        private String description;
    }

    @Getter
    @AllArgsConstructor
    private enum TransferButton {
        TO_SELF("Себе"),
        TO_USER("Другому");

        private String description;
    }

    @Getter
    @AllArgsConstructor
    private enum UserTransferButton {
        PHONE("По номеру"),
        ACCOUNT("По счету");

        private String description;
    }

    @Getter
    @AllArgsConstructor
    private enum Stage {
        TRANSFER("Между счетами или другому"),

        TO_SELF("На свой счет"),
        TO_SELF_FROM("На свой счет откуда"),
        TO_SELF_TO("На свой счет куда"),
        TO_SELF_AMOUNT("Сумма"),

        TO_USER("Другому"),
        TO_USER_FROM("С какого счета"),
        TO_USER_TO_ACC("Кому по номеру счета"),
        TO_USER_TO_PHONE("Кому по номеру телефона"),
        TO_USER_AMOUNT("Сумма"),
        TO_USER_CONFIRM("Подтверждение перевода");

        private String description;
    }

}
