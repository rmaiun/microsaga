package dev.rmaiun;

import dev.rmaiun.dto.CreateOrderDto;
import dev.rmaiun.helper.CreateOrderHelper;
import dev.rmaiun.services.Catalog;
import dev.rmaiun.services.DeliveryService;
import dev.rmaiun.services.MoneyTransferService;
import dev.rmaiun.services.OrderService;
import java.util.HashMap;

public class Example {

  public static void main(String[] args) {
    Catalog catalog = new Catalog();
    catalog.addProduct("Samsung Galaxy", 10);
    catalog.addProduct("Iphone X", 10);
    OrderService orderService = new OrderService(catalog);

    HashMap<String, Integer> accounts = new HashMap<>();
    accounts.put("user1", 1000);
    accounts.put("user2", 1000);
    MoneyTransferService moneyTransferService = new MoneyTransferService(accounts);

    DeliveryService deliveryService = new DeliveryService();

    CreateOrderHelper createOrderHelper = new CreateOrderHelper(orderService, moneyTransferService, deliveryService);

    createOrderHelper.createOrder(new CreateOrderDto("user1", "Iphone X"));

  }
}
