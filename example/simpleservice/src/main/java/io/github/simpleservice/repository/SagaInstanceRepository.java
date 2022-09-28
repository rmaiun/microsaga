package io.github.simpleservice.repository;

import io.github.simpleservice.domain.SagaInstance;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

  @Query("from SagaInstance saga join fetch SagaInvocation invocation where saga.state = 'RETRY_PLANNED' and saga.retryAfter < :date")
  List<SagaInstance> listReadyForRetry(ZonedDateTime date);

  @Query("from SagaInstance saga join fetch SagaInvocation invocation where saga.sagaId = :sagaId")
  Optional<SagaInstance> findBySagaId(String sagaId);
}
