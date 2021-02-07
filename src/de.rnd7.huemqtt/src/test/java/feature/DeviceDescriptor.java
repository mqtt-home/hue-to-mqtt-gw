package feature;

public class DeviceDescriptor {

    public enum DeviceType {
        daylight
    }

    private DeviceType type;
    private String id;

    public DeviceType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DeviceDescriptor{" +
            "type=" + type +
            ", id='" + id + '\'' +
            '}';
    }
}
