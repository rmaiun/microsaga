package dev.rmaiun.services;

import dev.rmaiun.saga4j.support.NoResult;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeliveryService {

  private static final Logger LOG = LogManager.getLogger(DeliveryService.class);


  public NoResult registerDelivery(String person) {
    LOG.info("Hello, {}! Your delivery will be on place at {}", person, ZonedDateTime.now(ZoneOffset.UTC));
    return NoResult.instance();
  }

  public NoResult registerDeliveryWithWrongAddress(String person) {
    throw new RuntimeException("Invalid address");
  }

}
