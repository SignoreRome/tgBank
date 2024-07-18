package ru.olbreslavets.tgbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.olbreslavets.tgbank.dto.PaymentDto;
import ru.olbreslavets.tgbank.mapper.PaymentMapper;
import ru.olbreslavets.tgbank.repository.PaymentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public List<PaymentDto> getLastFiveByUser(Long tgId) {
        return repository.getAllByUser(tgId, PageRequest.of(0, 5)).stream()
                .map(p -> paymentMapper.toDto(p, tgId))
                .toList();
    }

}
