package io.github.rmaiun.microsaga.dto;

public class PaymentRequest {

  private final String name;
  private final int moneyToCharge;

  public PaymentRequest(String name, int moneyToCharge) {
    this.name = name;
    this.moneyToCharge = moneyToCharge;
  }

  public String getName() {
    return name;
  }

  public int getMoneyToCharge() {
    return moneyToCharge;
  }
}
