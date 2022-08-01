package dev.rmaiun.services;

import dev.rmaiun.dto.PaymentRequest;
import dev.rmaiun.saga4j.support.NoResult;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoneyTransferService {

  private static final Logger LOG = LogManager.getLogger(MoneyTransferService.class);
  private final Map<String, Integer> bankAccounts;
  private final AtomicInteger counter = new AtomicInteger(1);

  public MoneyTransferService(Map<String, Integer> bankAccounts) {
    this.bankAccounts = bankAccounts;
  }

  public NoResult processPayment(PaymentRequest paymentRequest) {
    LOG.info("executing processPayment");
    Integer foundMoney = bankAccounts.get(paymentRequest.getName());
    if (foundMoney == null || foundMoney < paymentRequest.getMoneyToCharge()) {
      throw new RuntimeException(String.format("%s has not enough of money", paymentRequest.getName()));
    } else {
      bankAccounts.merge(paymentRequest.getName(), paymentRequest.getMoneyToCharge(), (a, b) -> a - b);
      return NoResult.instance();
    }
  }

  public NoResult processLaggingPayment(PaymentRequest paymentRequest) {
    LOG.info("executing lagging processPayment {}", counter.get());
    if (counter.get() > 3) {
      counter.set(1);
      Integer foundMoney = bankAccounts.get(paymentRequest.getName());
      if (foundMoney == null || foundMoney < paymentRequest.getMoneyToCharge()) {
        throw new RuntimeException(String.format("%s has not enough of money", paymentRequest.getName()));
      } else {
        bankAccounts.merge(paymentRequest.getName(), paymentRequest.getMoneyToCharge(), (a, b) -> a - b);
      }
    } else {
      counter.incrementAndGet();
      throw new RuntimeException("Couldn't connect to payment gateway");
    }
    return NoResult.instance();
  }

}
