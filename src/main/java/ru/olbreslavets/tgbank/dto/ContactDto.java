package ru.olbreslavets.tgbank.dto;

public record ContactDto(
        String phone,
        String email,
        String address
) {
}
