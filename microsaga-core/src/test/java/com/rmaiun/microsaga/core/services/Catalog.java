package com.rmaiun.microsaga.core.services;

import java.util.HashMap;
import java.util.Map;

public class Catalog {

  private final Map<String, Integer> products = new HashMap<>();

  public void addProduct(String name, int qty) {
    products.merge(name, qty, Integer::sum);
  }

  public Integer getProduct(String name) {
    return products.get(name);
  }

  public void decreaseProductQty(String name) {
    Integer integer = products.get(name);
    if (integer == null || integer <= 0) {
      throw new RuntimeException("Product is absent");
    }
    products.put(name, integer - 1);
  }

  public void increaseProductQty(String name) {
    products.merge(name, 1, Integer::sum);
  }
}
