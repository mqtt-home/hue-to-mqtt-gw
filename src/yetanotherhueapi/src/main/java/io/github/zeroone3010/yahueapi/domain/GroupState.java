package io.github.zeroone3010.yahueapi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupState {
  @JsonProperty("all_on")
  private boolean allOn;
  @JsonProperty("any_on")
  private boolean anyOn;

  public boolean isAllOn() {
    return allOn;
  }

  public boolean isAnyOn() {
    return anyOn;
  }

  @Override
  public String toString() {
    return JsonStringUtil.toJsonString(this);
  }
}
