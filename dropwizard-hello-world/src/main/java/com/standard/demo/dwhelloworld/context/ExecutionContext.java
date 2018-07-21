package com.standard.demo.dwhelloworld.context;

import com.google.common.base.MoreObjects;

/**
 * represents business transaction context
 */
public class ExecutionContext {

  private String transactionId;
  private String actor;

  public ExecutionContext(
      final String transactionId,
      final String actor) {
    this.transactionId = transactionId;
    this.actor = actor;
  }

  public String getTransactionId() {
    return transactionId;
  }

  protected void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getActor() {
    return actor;
  }

  protected void setActor(String actor) {
    this.actor = actor;
  }


  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("transactionId", transactionId)
        .add("actor", actor)
        .toString();
  }
}
