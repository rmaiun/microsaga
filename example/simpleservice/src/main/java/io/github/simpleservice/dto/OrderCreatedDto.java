package io.github.simpleservice.dto;

import java.time.ZonedDateTime;

public record OrderCreatedDto(Long id, Long price, String client, ZonedDateTime availableForDelivery) {

}
