package io.github.simpleservice.dto;

public record ProcessPaymentDto(String from, String to, Long money, Long orderId, String sagaId) {

}
