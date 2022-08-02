package dev.rmaiun.microsaga;

import dev.rmaiun.microsaga.dto.CreateOrderDto;
import dev.rmaiun.microsaga.helper.CreateOrderHelper;
import dev.rmaiun.microsaga.services.BusinessLogger;
import dev.rmaiun.microsaga.services.Catalog;
import dev.rmaiun.microsaga.services.DeliveryService;
import dev.rmaiun.microsaga.services.MoneyTransferService;
import dev.rmaiun.microsaga.services.OrderService;
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
    BusinessLogger businessLogger = new BusinessLogger();
    CreateOrderHelper createOrderHelper = new CreateOrderHelper(orderService, moneyTransferService, deliveryService, businessLogger);

    // createOrderHelper.createOrder(new CreateOrderDto("user1", "Iphone X"));
    // createOrderHelper.createOrdersWithFailedDelivery(new CreateOrderDto("user1", "Iphone X"));
    createOrderHelper.createOrdersWithRetryCompensation(new CreateOrderDto("user1", "Iphone X"));
  }
}
