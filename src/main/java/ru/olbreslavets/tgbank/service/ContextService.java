package ru.olbreslavets.tgbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.olbreslavets.tgbank.dto.context.ContextDto;
import ru.olbreslavets.tgbank.dto.context.ContextUpdateDto;
import ru.olbreslavets.tgbank.entity.Context;
import ru.olbreslavets.tgbank.mapper.ContextMapper;
import ru.olbreslavets.tgbank.repository.ContextRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContextService {

    private final ContextRepository repository;
    private final ContextMapper mapper;

    public String getContextCommand(String chatId) {
        return repository.findById(chatId)
                .map(Context::getCommand)
                .map(c -> c.name())
                .orElse(null);
    }

    public void clear(String chatId) {
        repository.deleteById(chatId);
    }

    public Optional<ContextDto> findByChatId(String chatId) {
        return repository.findById(chatId)
                .map(mapper::toDto);
    }

    public ContextDto create(ContextDto dto) {
        var entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    public ContextDto update(String chatId, ContextUpdateDto dto) {
        var entity = repository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        mapper.updateEntity(entity, dto);

        return mapper.toDto(repository.save(entity));
    }

}
