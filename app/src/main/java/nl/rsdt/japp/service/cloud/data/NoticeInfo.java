package nl.rsdt.japp.service.cloud.data;

import java.util.Map;

import nl.rsdt.japp.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 11-9-2016
 * Description...
 */
public class NoticeInfo {

    protected String title;

    public String getTitle() {
        return title;
    }

    protected String body;

    public String getBody() {
        return body;
    }

    protected String icon;

    public String getIconName() {
        return icon;
    }

    public int getDrawable() {
       return parseDrawable(icon);
    }

    public static NoticeInfo parse(Map<String, String> data) {
        NoticeInfo buffer = new NoticeInfo();
        buffer.title = data.get("title");
        buffer.body = data.get("body");
        buffer.icon = data.get("icon");
        return buffer;
    }

    public static int parseDrawable(String icon) {
        switch (icon) {
            case "info":
                return R.drawable.ic_info_black_48dp;
            case "belangrijk":
                return R.drawable.ic_priority_high_black_48dp;
            default:
                return R.drawable.ic_info_black_48dp;
        }
    }

}
