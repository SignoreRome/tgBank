package ru.olbreslavets.tgbank.mapper;

import org.mapstruct.Mapper;
import ru.olbreslavets.tgbank.dto.AccountDto;
import ru.olbreslavets.tgbank.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toDto(Account account);

}
