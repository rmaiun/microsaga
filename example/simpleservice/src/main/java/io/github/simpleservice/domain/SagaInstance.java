package io.github.simpleservice.domain;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "saga_instance")
public class SagaInstance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "saga_id")
  private String sagaId;

  @Enumerated
  private SagaInstanceState state;

  private String input;

  @Column(name = "finished_at")
  private ZonedDateTime finishedAt;

  @Column(name = "retry_after")
  private ZonedDateTime retryAfter;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "sagaId")
  private Set<SagaInvocation> invocations = new HashSet<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSagaId() {
    return sagaId;
  }

  public void setSagaId(String sagaId) {
    this.sagaId = sagaId;
  }

  public SagaInstanceState getState() {
    return state;
  }

  public void setState(SagaInstanceState state) {
    this.state = state;
  }

  public String getInput() {
    return input;
  }

  public void setInput(String input) {
    this.input = input;
  }

  public ZonedDateTime getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(ZonedDateTime finishedAt) {
    this.finishedAt = finishedAt;
  }

  public ZonedDateTime getRetryAfter() {
    return retryAfter;
  }

  public void setRetryAfter(ZonedDateTime retryAfter) {
    this.retryAfter = retryAfter;
  }

  public Set<SagaInvocation> getInvocations() {
    return invocations;
  }

  public void setInvocations(Set<SagaInvocation> invocations) {
    this.invocations = invocations;
  }
}
