package ru.olbreslavets.tgbank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountDto(
        Long number,
        LocalDate dateBegin,
        LocalDate dateClose,
        BigDecimal balance,
        boolean isMain
) {
}
