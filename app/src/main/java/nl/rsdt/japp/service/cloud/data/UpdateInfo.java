package nl.rsdt.japp.service.cloud.data;

import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 5-8-2016
 * Description...
 */
public class UpdateInfo {

    public String type;

    public String dateOfCreation;

    public static UpdateInfo parse(Map<String, String> data) {
        UpdateInfo info = new UpdateInfo();
        info.type = data.get("type");
        info.dateOfCreation = data.get("dateOfCreation");
        return info;
    }

}
