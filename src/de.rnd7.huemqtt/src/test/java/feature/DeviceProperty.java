package feature;

public class DeviceProperty {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DeviceProperty{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
