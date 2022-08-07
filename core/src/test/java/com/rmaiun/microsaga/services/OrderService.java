package com.rmaiun.microsaga.services;

import com.rmaiun.microsaga.dto.ProductOrder;

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