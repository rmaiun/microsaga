package dev.rmaiun.microsaga;

import dev.rmaiun.microsaga.dto.CreateOrderDto;
import dev.rmaiun.microsaga.helper.CreateOrderHelper;
import dev.rmaiun.microsaga.services.BusinessLogger;
import dev.rmaiun.microsaga.services.Catalog;
import dev.rmaiun.microsaga.services.DeliveryService;
import dev.rmaiun.microsaga.services.MoneyTransferService;
import dev.rmaiun.microsaga.services.OrderService;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateOrderHelperTests {

  public static final String IPHONE_X = "Iphone X";
  public static final int DEFAULT_PRICE = 1000;
  public static final int DEFAULT_QTY = 10;
  public static final String USER_1 = "user1";

  @Test
  public void happyDayActionsTest() {

    Catalog catalog = new Catalog();
    catalog.addProduct("Samsung Galaxy", 10);
    catalog.addProduct(IPHONE_X, DEFAULT_QTY);
    OrderService orderService = new OrderService(catalog);

    HashMap<String, Integer> accounts = new HashMap<>();
    accounts.put(USER_1, DEFAULT_PRICE);
    accounts.put("user2", 1000);
    MoneyTransferService moneyTransferService = new MoneyTransferService(accounts);

    DeliveryService deliveryService = new DeliveryService();
    BusinessLogger businessLogger = new BusinessLogger();

    CreateOrderHelper createOrderHelper = new CreateOrderHelper(orderService, moneyTransferService, deliveryService, businessLogger);
    createOrderHelper.createOrder(new CreateOrderDto("user1", "Iphone X"));
    Assertions.assertEquals(900, accounts.get(USER_1));
    Assertions.assertEquals(9, catalog.getProduct(IPHONE_X));
  }

  @Test
  public void retryActionsTest() {
    Catalog catalog = new Catalog();
    catalog.addProduct("Samsung Galaxy", 10);
    catalog.addProduct(IPHONE_X, DEFAULT_QTY);
    OrderService orderService = new OrderService(catalog);

    HashMap<String, Integer> accounts = new HashMap<>();
    accounts.put(USER_1, DEFAULT_PRICE);
    accounts.put("user2", 1000);
    MoneyTransferService moneyTransferService = new MoneyTransferService(accounts);

    DeliveryService deliveryService = new DeliveryService();
    BusinessLogger businessLogger = new BusinessLogger();

    CreateOrderHelper createOrderHelper = new CreateOrderHelper(orderService, moneyTransferService, deliveryService, businessLogger);
    createOrderHelper.createOrdersWithRetryAction(new CreateOrderDto("user1", "Iphone X"));
    Assertions.assertEquals(900, accounts.get(USER_1));
    Assertions.assertEquals(9, catalog.getProduct(IPHONE_X));
  }

  @Test
  public void activatedCompensationsTest() {
    Catalog catalog = new Catalog();
    catalog.addProduct("Samsung Galaxy", 10);
    catalog.addProduct(IPHONE_X, DEFAULT_QTY);
    OrderService orderService = new OrderService(catalog);

    HashMap<String, Integer> accounts = new HashMap<>();
    accounts.put(USER_1, DEFAULT_PRICE);
    accounts.put("user2", 1000);
    MoneyTransferService moneyTransferService = new MoneyTransferService(accounts);

    DeliveryService deliveryService = new DeliveryService();
    BusinessLogger businessLogger = new BusinessLogger();

    CreateOrderHelper createOrderHelper = new CreateOrderHelper(orderService, moneyTransferService, deliveryService, businessLogger);
    createOrderHelper.createOrdersWithFailedDelivery(new CreateOrderDto("user1", "Iphone X"));
    Assertions.assertEquals(1000, accounts.get(USER_1));
    Assertions.assertEquals(catalog.getProduct(IPHONE_X), accounts.get("user1"));
  }

  @Test
  public void activatedRetriedCompensationsTest() {
    Catalog catalog = new Catalog();
    catalog.addProduct("Samsung Galaxy", 10);
    catalog.addProduct(IPHONE_X, DEFAULT_QTY);
    OrderService orderService = new OrderService(catalog);

    HashMap<String, Integer> accounts = new HashMap<>();
    accounts.put(USER_1, DEFAULT_PRICE);
    accounts.put("user2", 1000);
    MoneyTransferService moneyTransferService = new MoneyTransferService(accounts);

    DeliveryService deliveryService = new DeliveryService();
    BusinessLogger businessLogger = new BusinessLogger();

    CreateOrderHelper createOrderHelper = new CreateOrderHelper(orderService, moneyTransferService, deliveryService, businessLogger);
    createOrderHelper.createOrdersWithRetryCompensation(new CreateOrderDto("user1", "Iphone X"));
    Assertions.assertEquals(1000, accounts.get(USER_1));
    Assertions.assertEquals(catalog.getProduct(IPHONE_X), accounts.get("user1"));
  }
}
