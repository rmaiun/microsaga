package io.github.simpleservice.service;

import io.github.simpleservice.dto.PlanDeliveryDto;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

  private final Set<String> availableCities = Set.of("Kyiv", "NewYork", "Praha");

  public void planDelivery(PlanDeliveryDto dto) {
    if (!availableCities.contains(dto.address())) {
      throw new RuntimeException(String.format("Failed to plan delivery to %s for user %s", dto.address(), dto.user()));
    }
  }
}
