package ru.olbreslavets.tgbank.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum Commands {

    START("/start", "Запуск бота"),
    DATA("/data", "Мои данные"),
    FINANCE("/finance", "Мои финансы"),
    TRANSFER("/transfer", "Переводы"),
    HISTORY("/history", "История операций"),
    INFO("/info", "Информация о банке"),
    HELP("/help", "Помощь"),
    UNKNOWN("", "");

    private String command;
    private String description;
    private static Set<Commands> commands = Set.of(Commands.values());

    public static Commands getByCommand(String command) {
        return commands.stream()
                .filter(c -> c.command.equals(command))
                .findAny()
                .orElse(UNKNOWN);
    }

}
