package ru.olbreslavets.tgbank.telegram;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olbreslavets.tgbank.config.BotConfig;
import ru.olbreslavets.tgbank.constant.Commands;
import ru.olbreslavets.tgbank.service.ContextService;
import ru.olbreslavets.tgbank.telegram.handler.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ContextService contextService;
    private final List<CommandHandler> commandHandlers;

    private Map<String, CommandHandler> mapCommandHandlers;

    @PostConstruct
    public void init() {
        List<BotCommand> commands = List.of(
                new BotCommand(Commands.START.getCommand(), Commands.START.getDescription()),
                new BotCommand(Commands.DATA.getCommand(), Commands.DATA.getDescription()),
                new BotCommand(Commands.FINANCE.getCommand(), Commands.FINANCE.getDescription()),
                new BotCommand(Commands.TRANSFER.getCommand(), Commands.TRANSFER.getDescription()),
                new BotCommand(Commands.HISTORY.getCommand(), Commands.HISTORY.getDescription()),
                new BotCommand(Commands.INFO.getCommand(), Commands.INFO.getDescription()),
                new BotCommand(Commands.HELP.getCommand(), Commands.HELP.getDescription())
        );
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        mapCommandHandlers = commandHandlers.stream()
                .collect(Collectors.toMap(CommandHandler::getCommand, Function.identity()));
    }

    @Override
    public void onUpdateReceived(Update update) {
        CommandHandler handler;

        if (update.hasMessage()) {
            var chatId = String.valueOf(update.getMessage().getChatId());
            var contextCommand = contextService.getContextCommand(chatId);

            if (update.getMessage().isCommand() || StringUtils.isBlank(contextCommand)) {
                contextService.clear(chatId);
                var command = update.getMessage().getText();
                handler = mapCommandHandlers.get(Commands.getByCommand(command).name());
            } else {
                handler = mapCommandHandlers.get(contextCommand);
            }

            try {
                handler.handleMessage(update.getMessage(), this);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            var message = update.getCallbackQuery().getMessage();
            var chatId = String.valueOf(message.getChatId());
            var contextCommand = contextService.getContextCommand(chatId);

            if (StringUtils.isNotBlank(contextCommand)) {
                handler = mapCommandHandlers.get(contextCommand);
                try {
                    handler.handleCallbackQuery(update.getCallbackQuery(),this);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

}
