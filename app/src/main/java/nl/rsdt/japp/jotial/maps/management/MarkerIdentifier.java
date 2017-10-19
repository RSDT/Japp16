package nl.rsdt.japp.jotial.maps.management;

import java.util.HashMap;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 17-9-2016
 * Description...
 */
public class MarkerIdentifier {

    public static final String TYPE_VOS = "VOS";

    public static final String TYPE_HUNTER = "HUNTER";

    public static final String TYPE_FOTO = "FOTO";

    public static final String TYPE_SC = "SC";

    public static final String TYPE_SC_CLUSTER = "SC_CLUSTER";

    public static final String TYPE_SIGHTING = "SIGHTING";

    public static final String TYPE_PIN = "PIN";

    public static final String TYPE_NAVIGATE = "NAVIGATE";

    public static final String TYPE_ME = "ME";
    public static final String TYPE_NAVIGATE_CAR = "NAVIGATE_CAR";

    private String type;

    public String getType() {
        return type;
    }

    private HashMap<String, String> properties = new HashMap<>();

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public static class Builder {
        MarkerIdentifier identifier = new MarkerIdentifier();

        public Builder setType(String type) {
            identifier.type = type;
            return this;
        }

        public Builder add(String key, String value) {
            identifier.properties.put(key, value);
            return this;
        }

        public Builder remove(String key) {
            identifier.properties.remove(key);
            return this;
        }

        public MarkerIdentifier create() {
            return identifier;
        }
    }

}
