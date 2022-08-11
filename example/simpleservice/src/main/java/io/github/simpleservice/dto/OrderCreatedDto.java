package io.github.simpleservice.dto;

import java.time.ZonedDateTime;

public record OrderCreatedDto(Long id, ZonedDateTime availableForDelivery) {

}
