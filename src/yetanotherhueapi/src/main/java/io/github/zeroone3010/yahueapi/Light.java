package io.github.zeroone3010.yahueapi;

public interface Light {
  /**
   * Returns the name of the light, as set by the user.
   *
   * @return Name of the light.
   */
  String getName();

  /**
   * Turns the light on.
   */
  void turnOn();

  /**
   * Turns the light off.
   */
  void turnOff();

  /**
   * Queries the light state -- or returns the cached state if caching
   * has been enabled with {@link Hue#setCaching(boolean)}.
   *
   * @return True if the light is on, false if it is off.
   */
  boolean isOn();

  /**
   * Queries the reachability of the light -- or returns the cached state if caching
   * has been enabled with {@link Hue#setCaching(boolean)}.
   *
   * @return True if the light is reachable, false if it is not.
   */
  boolean isReachable();

  /**
   * Sets the brightness of the light. If the light is off, does not turn it on, nor does {@code 0} turn it off.
   *
   * @param brightness A value from {@code 0} (minimum brightness) to {@code 254} (maximum brightness).
   */
  void setBrightness(int brightness);

  /**
   * Returns info on the type of the light.
   *
   * @return An enum value specifying the color and dimming capabilities of the light.
   */
  LightType getType();

  /**
   * Sets a state for the light.
   *
   * @param state A state to be set for this light.
   */
  void setState(State state);

  /**
   * Gets the state of the light -- or returns the cached state if caching
   * has been enabled with {@link Hue#setCaching(boolean)}.
   *
   * @return The current state of the light.
   */
  State getState();
}
