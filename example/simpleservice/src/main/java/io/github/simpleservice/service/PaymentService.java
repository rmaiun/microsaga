package io.github.simpleservice.service;

import io.github.simpleservice.domain.Account;
import io.github.simpleservice.domain.Payment;
import io.github.simpleservice.dto.PaymentProcessedDto;
import io.github.simpleservice.dto.ProcessPaymentDto;
import io.github.simpleservice.repository.AccountRepository;
import io.github.simpleservice.repository.PaymentRepository;
import java.time.ZonedDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  private final AccountRepository accountRepository;
  private final PaymentRepository paymentRepository;

  public PaymentService(AccountRepository accountRepository, PaymentRepository paymentRepository) {
    this.accountRepository = accountRepository;
    this.paymentRepository = paymentRepository;
  }

  @Transactional
  public PaymentProcessedDto processPayment(ProcessPaymentDto dto, String city) {
    var acc1 = accountRepository.findByCode(dto.from())
        .orElseThrow(() -> new RuntimeException(String.format(" account %s is not found", dto.from())));
    var acc2 = accountRepository.findByCode(dto.to())
        .orElseThrow(() -> new RuntimeException(String.format(" account %s is not found", dto.to())));
    if (acc1.getAmount() > dto.money()) {
      acc1.setAmount(acc1.getAmount() - dto.money());
      accountRepository.save(acc1);
      acc2.setAmount(acc2.getAmount() + dto.money());
      accountRepository.save(acc2);
      var payment = new Payment();
      payment.setAccFrom(acc1.getId());
      payment.setAccTo(acc2.getId());
      payment.setAmount(dto.money());
      payment.setSagaId(dto.sagaId());
      payment.setOrderId(dto.orderId());
      paymentRepository.save(payment);
      return new PaymentProcessedDto(city, ZonedDateTime.now());
    } else {
      throw new RuntimeException(String.format("User %s can't pay %d money because he has only %d on his account",
          acc1.getCode(), dto.money(), acc1.getAmount()));
    }
  }

  @Transactional
  public void cancelPayment(String sagaId) {
    var accounts = StreamSupport.stream(accountRepository.findAll().spliterator(), false)
        .collect(Collectors.toMap(Account::getId, Function.identity()));
    var payments = paymentRepository.findAllBySagaId(sagaId);
    for (Payment payment : payments) {
      var from = accounts.get(payment.getAccFrom());
      var to = accounts.get(payment.getAccTo());
      from.setAmount(from.getAmount() + payment.getAmount());
      to.setAmount(to.getAmount() - payment.getAmount());
    }
    paymentRepository.deleteAllBySagaId(sagaId);
    accountRepository.saveAll(accounts.values().stream().toList());
  }
}
