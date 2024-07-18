package ru.olbreslavets.tgbank.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.olbreslavets.tgbank.entity.Payment;
import ru.olbreslavets.tgbank.repository.AccountRepository;
import ru.olbreslavets.tgbank.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public boolean isAvailable(Long tgId) {
        int count = accountRepository.countByUser(tgId);

        return count > 1 ? true : false;
    }

    @Transactional
    public void transferByNums(@NotNull Long from, @NotNull Long to, @NotNull BigDecimal amount, @NotBlank String purpose) {
        var accFrom = accountRepository.findByNumber(from)
                .orElseThrow(() -> new IllegalArgumentException("Аккаунта " + from.toString() + " не существует"));
        var accTo = accountRepository.findByNumber(to)
                .orElseThrow(() -> new IllegalArgumentException("Аккаунта " + to.toString() + " не существует"));

        if ((accFrom.getBalance().compareTo(amount)) < 0) {
            throw new IllegalArgumentException("На аккаунте " + from.toString() + " недостаточно средств");
        }

        BigDecimal temp;

        temp = accFrom.getBalance().subtract(amount);
        accFrom.setBalance(temp);

        temp = accTo.getBalance().add(amount);
        accTo.setBalance(temp);

        accountRepository.save(accFrom);
        accountRepository.save(accTo);

        var payment = new Payment();
        payment.setPayer(accFrom);
        payment.setPayee(accTo);
        payment.setAmount(amount);
        payment.setPurpose(purpose);
        payment.setStatus("PROCESSED");
        payment.setOperDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

}
