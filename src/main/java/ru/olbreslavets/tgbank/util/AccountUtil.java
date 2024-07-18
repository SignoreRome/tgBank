package ru.olbreslavets.tgbank.util;

import lombok.experimental.UtilityClass;
import ru.olbreslavets.tgbank.dto.AccountDto;

import java.util.List;

@UtilityClass
public class AccountUtil {

    public String getAccountsText(String header, List<AccountDto> accounts) {
        StringBuilder builder = new StringBuilder();
        builder.append(header);
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        for (int i = 0; i < accounts.size(); i++) {
            var a = accounts.get(i);
            var text = "%s   Номер счета: %s, Баланс: %s".formatted(i + 1, a.number(), a.balance().toString());
            builder.append(text);
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

}
