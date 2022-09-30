package io.github.simpleservice.repository;

import io.github.simpleservice.domain.SagaInstance;
import io.github.simpleservice.domain.SagaInstanceState;
import io.github.simpleservice.domain.SagaInvocation;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

  List<SagaInstance> findAllByStateAndRetryAfterBefore(SagaInstanceState state, ZonedDateTime date);

  @Query("from SagaInvocation si where si.sagaId = ?1")
  List<SagaInvocation> findSagaInvocations(String sagaId);

  Optional<SagaInstance> findSagaInstanceBySagaId(String sagaId);
}
