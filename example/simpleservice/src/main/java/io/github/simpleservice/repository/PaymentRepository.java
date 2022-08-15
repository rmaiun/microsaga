package io.github.simpleservice.repository;

import io.github.simpleservice.domain.Payment;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, String> {

  List<Payment> findAllBySagaId(String sagaId);

  void deleteAllBySagaId(String sagaId);
}
