package io.github.simpleservice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;
  @Column(name = "acc_from")
  private Long accFrom;
  @Column(name = "acc_to")
  private Long accTo;
  private Long amount;
  @Column(name = "order_id")
  private Long orderId;
  @Column(name = "saga_id")
  private String sagaId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getAccFrom() {
    return accFrom;
  }

  public void setAccFrom(Long accFrom) {
    this.accFrom = accFrom;
  }

  public Long getAccTo() {
    return accTo;
  }

  public void setAccTo(Long accTo) {
    this.accTo = accTo;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getSagaId() {
    return sagaId;
  }

  public void setSagaId(String sagaId) {
    this.sagaId = sagaId;
  }
}
