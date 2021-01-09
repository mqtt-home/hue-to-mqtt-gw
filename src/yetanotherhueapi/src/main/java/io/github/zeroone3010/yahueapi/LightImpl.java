package io.github.zeroone3010.yahueapi;

import io.github.zeroone3010.yahueapi.domain.LightDto;
import io.github.zeroone3010.yahueapi.domain.LightState;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

final class LightImpl implements Light {
  private static final Logger logger = Logger.getLogger("LightImpl");

  private final String id;
  private final String name;
  private final Supplier<LightState> stateProvider;
  private final Function<State, String> stateSetter;
  private final LightType type;

  LightImpl(final String id, final LightDto light, final Supplier<LightState> stateProvider,
            final Function<State, String> stateSetter) {
    this.id = id;
    if (light == null) {
      throw new HueApiException("Light " + id + " cannot be found.");
    }
    this.name = light.getName();
    this.stateProvider = stateProvider;
    this.stateSetter = stateSetter;
    this.type = LightType.parseTypeString(light.getType());
  }

  protected String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void turnOn() {
    setState(((StateBuilderSteps.OnOffStep) State.builder()).on());
  }

  @Override
  public void turnOff() {
    setState(((StateBuilderSteps.OnOffStep) State.builder()).off());
  }

  @Override
  public boolean isOn() {
    return getLightState().isOn();
  }

  @Override
  public boolean isReachable() {
    return getLightState().isReachable();
  }

  private LightState getLightState() {
    final LightState state = stateProvider.get();
    logger.fine(state.toString());
    return state;
  }

  @Override
  public void setBrightness(final int brightness) {
    setState(((StateBuilderSteps.BrightnessStep) State.builder()).brightness(brightness).keepCurrentState());
  }

  @Override
  public void setState(final State state) {
    final String result = stateSetter.apply(state);
    logger.fine(result);
  }

  @Override
  public LightType getType() {
    return type;
  }

  @Override
  public State getState() {
    return State.build(getLightState());
  }

  @Override
  public String toString() {
    return "Light{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", type=" + type +
        '}';
  }
}
