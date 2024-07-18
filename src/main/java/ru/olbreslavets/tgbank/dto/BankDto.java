package ru.olbreslavets.tgbank.dto;

public record BankDto(
        String bik,
        String name,
        String legalAddress,
        String corrAcc,
        ContactDto contact
) {
}
