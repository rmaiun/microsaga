package io.github.rmaiun.microsaga.dto;

public class CreateOrderDto {

  private final String person;
  private final String product;

  public CreateOrderDto(String person, String product) {
    this.person = person;
    this.product = product;
  }

  public String getPerson() {
    return person;
  }

  public String getProduct() {
    return product;
  }
}
