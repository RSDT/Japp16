package nl.rsdt.japp.service.cloud.data;

import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 5-8-2016
 * Description...
 */
public class UpdateInfo {

    public static final String ACTION_NEW = "new";

    public static final String ACTION_UPDATE = "update";

    public String type;

    public String action;

    public static UpdateInfo parse(Map<String, String> data) {
        UpdateInfo info = new UpdateInfo();
        info.type = data.get("type");
        info.action = data.get("action");
        return info;
    }

}
