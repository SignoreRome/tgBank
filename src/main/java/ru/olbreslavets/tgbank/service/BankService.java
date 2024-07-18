package ru.olbreslavets.tgbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.olbreslavets.tgbank.dto.BankDto;
import ru.olbreslavets.tgbank.mapper.BankMapper;
import ru.olbreslavets.tgbank.repository.BankRepository;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankMapper bankMapper;

    @Transactional(readOnly = true)
    public BankDto getBankInfo() {
        var entity = bankRepository.findByName("tgBank");
        return bankMapper.map(entity);
    }

}
