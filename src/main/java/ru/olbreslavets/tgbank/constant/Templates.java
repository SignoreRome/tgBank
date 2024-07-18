package ru.olbreslavets.tgbank.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Templates {
    public static final String GREETING_TEMP = """
            Добрый день, %s, это чат-бот банка ООО "ВАШ БАНК".
            """;
    public static final String AUTH_PHONE = "Пожалуйста, отправьте ваш номер телефона для авторизации";

    public static final String ERROR_AUTH_TEMP = "%s, вы не авторизовалось, пожалуйста, выполните команду /start";
    public static final String ERROR = "Произошла ошибка, пожалуйста повторите операцию";

    public static final String CANCELED = "Операция отменена";
    public static final String SUCCEED = "Операция выполнена";

    public static final String HISTORY = "История операций:";
    public static final String FINANCE = "Мои финансы:";

    public static final String PAYMENT_PDF_TEMP = "PAYMENT/Operation date: %s/Operation amount: %s/Purpose: %s/From: %s/To: %s";
    public static final String PAYMENT_TEMP = "%s   %s   %s  %s. Откуда: %s. Куда: %s.";
    public static final String ACCOUNT_TEMP = "Номер счета: %s, Баланс: %s%s";
    public static final String USER_TEMP = """
            Данные пользователя:
            
            ФИО: %s
            Дата рождения: %s
            Номер телефона: %s
            """;
    public static final String INFO_TEMP = """
            Информация о банке
            
            БИК: %s
            Наименование: %s
            Юридический адрес: %s
            Номер телефона: %s
            email: %s
            """;

    public static final String USER_NOT_FOUND = "Пользователь с эти номером телефона не найден. Пожалуйста, обратитесь в банк для регистрации или смены номера телефона";

    public static final String UNKNOWN = "Неизвестная команда";

    public static final String ENTER_TRANSFER_AMOUNT = "Введите сумму перевода";

    public static final String NO_TRANSFER = "Раздел переводов недоступен, так как у вас нет ни одного открытого счета";

    public static final String ENTER_PHONE = "Напишите номер пользователя в формате +7ХХХХХХХХХХ";

    public static final String SELF_TRANSFER_CONFIRM = """
            Подтвердите данные перевода:
            
            Счет отправитель: %s
            Счет получатель: %s
            Сумма: %s руб
            """;

    public static final String USER_TRANSFER_CONFIRM = """
            Подтвердите данные перевода:
            
            Счет отправитель: %s
            Получатель: %s
            Сумма: %s руб
            """;

    public static final String ENTER_CORRECT_AMOUNT = "Введите корректное значение формата XXX.XX";

    public static final String GENERATED_PAYMENT = """
            Номер операции - %s
            Приложен файл с платежным поручением. Для просмотра необходимо скачать
            """;


}
