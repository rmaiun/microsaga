package io.github.simpleservice.repository;

import io.github.simpleservice.domain.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

  @Modifying
  @Query("DELETE from Order o where o.sagaId = ?1")
  void deleteAllBySagaId(String sagaId);

}
