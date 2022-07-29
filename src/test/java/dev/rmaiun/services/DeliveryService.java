package dev.rmaiun.services;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeliveryService {

  private static final Logger LOG = LogManager.getLogger(DeliveryService.class);


  public boolean registerDelivery(String person) {
    LOG.info("Hello, {}! Your delivery will be on place at {}", person, ZonedDateTime.now(ZoneOffset.UTC));
    return true;
  }

  public void registerDeliveryWithWrongAddress(String person) {
    throw new RuntimeException("Invalid address");
  }

}
