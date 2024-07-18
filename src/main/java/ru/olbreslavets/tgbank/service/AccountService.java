package ru.olbreslavets.tgbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.olbreslavets.tgbank.dto.AccountDto;
import ru.olbreslavets.tgbank.mapper.AccountMapper;
import ru.olbreslavets.tgbank.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;

    @Transactional(readOnly = true)
    public List<AccountDto> getAll(Long tgId) {
        return repository.getAllByUser(tgId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isExistByNumber(Long number) {
        return repository.existsByNumber(number);
    }

    @Transactional(readOnly = true)
    public Optional<AccountDto> findMainByUser(Long tgId) {
        var mains = repository.getMainByUser(tgId);

        return switch (mains.size()) {
            case 0 -> repository.getAllByUser(tgId).stream().findFirst().map(mapper::toDto);
            default -> mains.stream().findFirst().map(mapper::toDto);
        };
    }

    @Transactional(readOnly = true)
    public Optional<AccountDto> findMainByUserPhone(String phone) {
        var mains = repository.getMainByUserPhone(phone);

        return switch (mains.size()) {
            case 0 -> repository.getAllByUserPhone(phone).stream().findFirst().map(mapper::toDto);
            default -> mains.stream().findFirst().map(mapper::toDto);
        };
    }

}
