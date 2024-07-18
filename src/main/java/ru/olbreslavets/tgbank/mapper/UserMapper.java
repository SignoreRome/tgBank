package ru.olbreslavets.tgbank.mapper;

import org.mapstruct.Mapper;
import ru.olbreslavets.tgbank.dto.UserDto;
import ru.olbreslavets.tgbank.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto source);

    UserDto toDto(User source);

    List<UserDto> toDtoList(List<User> source);

}
