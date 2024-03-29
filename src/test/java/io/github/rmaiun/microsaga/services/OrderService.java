package io.github.rmaiun.microsaga.services;

import io.github.rmaiun.microsaga.dto.ProductOrder;

public class OrderService {

  private final Catalog catalog;

  public OrderService(Catalog catalog) {
    this.catalog = catalog;
  }

  public ProductOrder makeOrder(String product) {
    catalog.decreaseProductQty(product);
    return new ProductOrder(product, 1);
  }

  public void cancelOrder(String product) {
    catalog.increaseProductQty(product);
  }

}
