package io.github.zeroone3010.yahueapi;

import java.awt.Color;
import java.util.List;

public final class StateBuilderSteps {
  private StateBuilderSteps() {
    // prevent instantiation
  }

  public interface InitialStep extends SceneStep, HueStep, XyStep, ColorStep, ColorTemperatureStep {

  }

  public interface HueStep {
    /**
     * Hue.
     *
     * @param hue A value from {@code 0} to {@code 65280}.
     * @return The next step of this state builder
     */
    SaturationStep hue(int hue);
  }

  public interface SaturationStep {
    /**
     * Saturation.
     *
     * @param saturation A value from 0 (white) to 254 (most saturated).
     * @return The next step of this state builder
     */
    BrightnessStep saturation(int saturation);
  }

  public interface BrightnessStep {
    /**
     * Brightness.
     *
     * @param brightness A value from {@code 1} (minimum brightness) to {@code 254} (maximum brightness).
     * @return The next step of this state builder
     */
    BuildStep brightness(int brightness);
  }

  public interface XyStep {
    /**
     * Color.
     *
     * @param xy The x and y coordinates of the C.I.E. chromaticity diagram. Exactly two values between 0 and 1 required.
     * @return The next step of this state builder
     */
    BrightnessStep xy(List<Float> xy);
  }

  public interface ColorStep {
    /**
     * Color.
     *
     * @param color The color as a {@link java.awt.Color} object.
     * @return The next step of this state builder
     */
    BuildStep color(Color color);

    /**
     * Color.
     *
     * @param color The color as a hexadecimal string, for example "#ff0000" for red.
     * @return The next step of this state builder
     */
    BuildStep color(String color);
  }

  public interface ColorTemperatureStep {
    /**
     * Color temperature.
     *
     * @param colorTemperature The color temperature in mireks. Must be between {@code 153} (6500K) and {@code 500} (2000K)
     * @return The next step of this state builder
     */
    BrightnessStep colorTemperatureInMireks(int colorTemperature);
  }

  public interface TransitionTimeStep {
    /**
     * Transition time.
     *
     * @param tenths Transition time in tenths of seconds, i.e. "4" equals "0.4 seconds".
     * @return The next step of this state builder
     */
    OnOffStep transitionTime(int tenths);
  }

  public interface SceneStep {
    /**
     * Scene.
     *
     * @param scene An identifier of a scene to be activated.
     * @return The next step of this state builder
     */
    OnOffStep scene(String scene);
  }

  public interface BuildStep extends OnOffStep, TransitionTimeStep {

  }

  public interface OnOffStep {
    State on(Boolean on);

    /**
     * Creates a new {@code State} that will turn on the light to which it is assigned.
     * A shorthand method for {@code on(true)}. Avoid calling in vain if you know the light is on already.
     *
     * @return A new {@code State}.
     */
    default State on() {
      return on(true);
    }

    /**
     * Creates a new {@code State} that will turn off the light to which it is assigned.
     * A shorthand method for {@code on(false)}.
     *
     * @return A new {@code State}.
     */
    default State off() {
      return on(false);
    }

    /**
     * Keeps the current state of the light to which this state is assigned: if it's on,
     * it stays on, and if it's off, it stays off.
     * A shorthand method for {@code on(null)}.
     *
     * @return A new {@code State}.
     */
    default State keepCurrentState() {
      return on(null);
    }
  }
}
