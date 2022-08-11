package io.github.simpleservice.dto;

import java.time.ZonedDateTime;

public record PaymentProcessedDto(String status, ZonedDateTime timestamp) {

}
