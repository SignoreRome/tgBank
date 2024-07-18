package ru.olbreslavets.tgbank.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;

public record UserDto(
        Long tgId,
        String fio,
        String phone,
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate birthDate
) {
}
