package io.github.simpleservice.service;

import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import io.github.simpleservice.dto.PlanDeliveryDto;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {
  public static final Logger LOG = LogManager.getLogger(DeliveryService.class);
  private final Set<String> availableCities = Set.of("Kyiv", "NewYork", "Praha");

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void planDelivery(PlanDeliveryDto dto) {
    LOG.info("Calling DeliveryService.planDelivery");
    if (!availableCities.contains(dto.address())) {
      throw new RuntimeException(String.format("Failed to plan delivery to %s for user %s", dto.address(), dto.user()));
    }
  }
}
