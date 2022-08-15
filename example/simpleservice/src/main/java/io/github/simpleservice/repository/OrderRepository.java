package io.github.simpleservice.repository;

import io.github.simpleservice.domain.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

  void deleteAllBySagaId(String sagaId);

}
