package nl.rsdt.japp.jotial.maps;

import nl.rsdt.japp.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 28-9-2016
 * Description...
 */

public class MapStyle {

    public static final int AUTO = 1;

    public static final int DAY = 2;

    public static final int NIGHT = 3;


    public static int getAssociatedRaw(int style) {
        switch (style) {
            case MapStyle.AUTO:
                break;
            case MapStyle.DAY:
                return R.raw.map_style_day;
            case MapStyle.NIGHT:
                return R.raw.map_style_night;
        }
        return R.raw.map_style_day;
    }
}
