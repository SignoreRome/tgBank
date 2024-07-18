package ru.olbreslavets.tgbank.util;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtil {

    public static final DateTimeFormatter dd_MM_yyyy_WITH_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static final DateTimeFormatter dd_MM_yyyy = DateTimeFormatter.ofPattern("dd-MM-yyyy");

}
