package dev.rmaiun.services;

import dev.rmaiun.dto.PaymentRequest;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MoneyTransferService {

  private final Map<String, Integer> bankAccounts;
  private final AtomicInteger counter = new AtomicInteger();

  public MoneyTransferService(Map<String, Integer> bankAccounts) {
    this.bankAccounts = bankAccounts;
  }

  public boolean processPayment(PaymentRequest paymentRequest) {
    Integer foundMoney = bankAccounts.get(paymentRequest.getName());
    if (foundMoney == null || foundMoney < paymentRequest.getMoneyToCharge()) {
      throw new RuntimeException(String.format("%s has not enough of money", paymentRequest.getName()));
    } else {
      bankAccounts.merge(paymentRequest.getName(), paymentRequest.getMoneyToCharge(), (a, b) -> a - b);
      return true;
    }
  }

  public void processLaggingPayment(PaymentRequest paymentRequest) {
    if (counter.get() > 3) {
      counter.set(0);
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
  }

}
