package ru.olbreslavets.tgbank.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDto {

    private Long id;
    private String amount;
    private String status;
    private String purpose;
    private Long from;
    private Long to;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime operDate;

}
