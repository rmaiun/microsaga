package io.github.simpleservice.repository;

import io.github.simpleservice.domain.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

  List<Payment> findAllBySagaId(String sagaId);

  @Modifying
  @Query("DELETE from Payment p where p.sagaId = ?1")
  void deleteAllBySagaId(String sagaId);
}
