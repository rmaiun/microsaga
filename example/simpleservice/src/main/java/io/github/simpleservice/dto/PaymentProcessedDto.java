package io.github.simpleservice.dto;

import java.time.ZonedDateTime;

public record PaymentProcessedDto(ZonedDateTime timestamp, String payer) {

}
