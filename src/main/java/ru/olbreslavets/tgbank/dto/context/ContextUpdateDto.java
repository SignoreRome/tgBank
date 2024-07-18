package ru.olbreslavets.tgbank.dto.context;

public record ContextUpdateDto(
        String stage,
        String messageIn,
        String messageOut
) {
}
