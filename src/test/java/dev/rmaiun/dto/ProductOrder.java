package dev.rmaiun.dto;

public class ProductOrder {

  private String name;
  private int qty;

  public ProductOrder(String name, int qty) {
    this.name = name;
    this.qty = qty;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getQty() {
    return qty;
  }

  public void setQty(int qty) {
    this.qty = qty;
  }
}
