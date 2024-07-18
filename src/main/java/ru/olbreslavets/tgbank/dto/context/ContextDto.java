package ru.olbreslavets.tgbank.dto.context;

import ru.olbreslavets.tgbank.constant.Commands;

public record ContextDto(
        String chatId,
        Commands command,
        String stage,
        String messageIn,
        String messageOut
) {
}
