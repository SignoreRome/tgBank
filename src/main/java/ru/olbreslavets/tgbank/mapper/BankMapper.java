package ru.olbreslavets.tgbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.olbreslavets.tgbank.dto.BankDto;
import ru.olbreslavets.tgbank.entity.Bank;

@Mapper(componentModel = "spring")
public interface BankMapper {

    @Mapping(target = "contact", source = "contacts")
    BankDto map(Bank bank);

}
