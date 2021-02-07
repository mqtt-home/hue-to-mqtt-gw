package features;

public class DeviceProperty {

    private String name;
    private String value;

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "DeviceProperty{" +
            "name='" + this.name + '\'' +
            ", value='" + this.value + '\'' +
            '}';
    }
}
