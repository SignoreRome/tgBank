package ru.olbreslavets.tgbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.olbreslavets.tgbank.dto.UserDto;
import ru.olbreslavets.tgbank.mapper.UserMapper;
import ru.olbreslavets.tgbank.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public void createUser(UserDto dto) {
        var newUser = mapper.toEntity(dto);
        repository.save(newUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findByPhone(String phone) {
        return repository.findByPhone(phone)
                .map(mapper::toDto);
    }

    @Transactional
    public UserDto updateTgId(String phone, Long tgId) {
        var entity = repository.findByPhone(phone).orElseThrow(() -> new RuntimeException());
        entity.setTgId(tgId);

        return mapper.toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findByTgId(Long tgId) {
        return repository.findByTgId(tgId)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findByAccountNumber(Long number) {
        return repository.findByAccountNumber(number)
                .map(mapper::toDto);
    }

}
