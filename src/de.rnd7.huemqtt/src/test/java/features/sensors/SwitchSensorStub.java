package features.sensors;

import com.google.common.collect.ImmutableList;
import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.Button;
import io.github.zeroone3010.yahueapi.ButtonEvent;
import io.github.zeroone3010.yahueapi.SensorType;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.SwitchEvent;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SwitchSensorStub extends SensorStub implements Switch {

    private int button;
    private int code;

    private static class ButtonStub implements Button {
        final int number;

        private ButtonStub(final int number) {
            this.number = number;
        }

        @Override
        public int getNumber() {
            return this.number;
        }

        @Override
        public List<ButtonEvent> getPossibleEvents() {
            return Collections.emptyList();
        }
    }

    private final Map<Integer, Button> buttons = ImmutableMap.of(
        0, new ButtonStub(0),
        1, new ButtonStub(1),
        2, new ButtonStub(2),
        3, new ButtonStub(3)
    );


    public SwitchSensorStub(final DeviceDescriptor device) {
        super(device);
    }

    @Override
    public SensorType getType() {
        return SensorType.SWITCH;
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);
        
        this.button = getInt(properties, "button", this.button);
        this.code = getInt(properties, "code", this.code);
    }

    @Override
    public List<Button> getButtons() {
        return ImmutableList.copyOf(this.buttons.values());
    }

    @Override
    public SwitchEvent getLatestEvent() {
        final SwitchEvent event = Mockito.mock(SwitchEvent.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(event.getButton())
            .thenReturn(this.buttons.get(this.button));
        Mockito.when(event.getAction().getEventType())
            .thenReturn(ButtonEvent.ButtonEventType.INITIAL_PRESS);

        Mockito.when(event.getAction().getEventCode())
            .thenReturn(this.code);
        return event;
    }
}
