package io.github.simpleservice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "saga_invocation")
public class SagaInvocation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "saga_id")
  private String sagaId;

  private String name;

  private boolean success;
  private boolean compensation;

  private String result;

  public SagaInvocation() {
  }

  public SagaInvocation(String sagaId, String name, boolean success, boolean compensation, String result) {
    this.sagaId = sagaId;
    this.name = name;
    this.success = success;
    this.compensation = compensation;
    this.result = result;
  }

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public boolean isCompensation() {
    return compensation;
  }

  public void setCompensation(boolean compensation) {
    this.compensation = compensation;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
