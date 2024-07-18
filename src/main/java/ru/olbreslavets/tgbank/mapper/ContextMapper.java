package ru.olbreslavets.tgbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.olbreslavets.tgbank.dto.context.ContextDto;
import ru.olbreslavets.tgbank.dto.context.ContextUpdateDto;
import ru.olbreslavets.tgbank.entity.Context;

@Mapper(componentModel = "spring")
public interface ContextMapper {

    Context toEntity(ContextDto source);

    ContextDto toDto(Context context);

    void updateEntity(@MappingTarget Context source, ContextUpdateDto target);

}
