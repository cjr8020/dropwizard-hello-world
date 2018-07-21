package com.standard.demo.dwhelloworld.da.entity;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;

/**
 * Actor entity - represents application user entity as it is persisted in the DB
 */
public class Actor {

  private Long id;
  private String username;
  private Integer resourcesRequested = 0;

  // audit columns
  private Integer recordVersion = 0;
  private String createdBy;
  private LocalDateTime createdTimestamp;
  private String updatedBy;
  private LocalDateTime updatedTimestamp;

  public Actor() {}

  public Actor(
      final String username,
      final String createdBy,
      final LocalDateTime createdTimestamp) {
    this.username = username;
    this.createdBy = createdBy;
    this.createdTimestamp = createdTimestamp;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Integer getResourcesRequested() {
    return resourcesRequested;
  }

  public void setResourcesRequested(Integer resourcesRequested) {
    this.resourcesRequested = resourcesRequested;
  }

  public void incrementNumberOfResourcesRequested() {
    ++this.resourcesRequested;
  }

  public Integer getRecordVersion() {
    return recordVersion;
  }

  public void setRecordVersion(Integer recordVersion) {
    this.recordVersion = recordVersion;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public LocalDateTime getUpdatedTimestamp() {
    return updatedTimestamp;
  }

  public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
    this.updatedTimestamp = updatedTimestamp;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("username", username)
        .add("resourcesRequested", resourcesRequested)
        .add("recordVersion", recordVersion)
        .add("createdBy", createdBy)
        .add("createdTimestamp", createdTimestamp)
        .add("updatedBy", updatedBy)
        .add("updatedTimestamp", updatedTimestamp)
        .toString();
  }
}
