package features;

public class DeviceDescriptor {

    public enum DeviceType {
        daylight,
        ambient,
        button
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
