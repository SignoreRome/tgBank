package ru.olbreslavets.tgbank.mapper;

import org.mapstruct.Mapper;
import ru.olbreslavets.tgbank.dto.ContactDto;
import ru.olbreslavets.tgbank.entity.Contact;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactDto map(Contact contact);
}
