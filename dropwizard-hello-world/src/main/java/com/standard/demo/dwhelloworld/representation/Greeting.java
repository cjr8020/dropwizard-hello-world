package com.standard.demo.dwhelloworld.representation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents Greeting resource
 */
public class Greeting {

  private String message;
  private String username;
  private Integer resourceRequests;

  public Greeting() {
    // Jackson deserialization
  }

  public Greeting(final String message) {
    this.message = message;
  }

  public Greeting(final String message, final String username, final Integer resourceRequests) {
    this.message = message;
    this.username = username;
    this.resourceRequests = resourceRequests;
  }

  @JsonProperty
  public String getMessage() {
    return message;
  }

  @JsonProperty
  public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty
  public String getUsername() {
    return username;
  }

  @JsonProperty
  public void setUsername(String username) {
    this.username = username;
  }

  @JsonProperty
  public Integer getResourceRequests() {
    return resourceRequests;
  }

  @JsonProperty
  public void setResourceRequests(Integer resourceRequests) {
    this.resourceRequests = resourceRequests;
  }
}
