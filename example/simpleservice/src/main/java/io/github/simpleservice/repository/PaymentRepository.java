package io.github.simpleservice.repository;

import io.github.simpleservice.domain.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, String> {

}
