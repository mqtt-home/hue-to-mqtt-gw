package features;

public class DeviceDescriptor {

    public enum DeviceType {
        ambient,
        button,
        daylight,
        ct_light,
        light,
        color_light,
        presence,
        temperature
    }

    private DeviceType type;
    private String id;

    public DeviceType getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "DeviceDescriptor{" +
            "type=" + this.type +
            ", id='" + this.id + '\'' +
            '}';
    }
}
