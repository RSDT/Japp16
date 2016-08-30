package nl.rsdt.japp.jotial.maps.management;

import java.util.Calendar;

import nl.rsdt.japp.jotial.Destroyable;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class MapItemDateControl implements Destroyable {

    protected Calendar lastUpdate = Calendar.getInstance();

    public Calendar getLastUpdateDate() {
        return lastUpdate;
    }

    protected boolean isElapsedSinceLastUpdate(long time) {
        Calendar now = Calendar.getInstance();
        long delta = now.getTimeInMillis() - lastUpdate.getTimeInMillis();
        return delta >= time;
    }

    @Override
    public void onDestroy() {
        lastUpdate = null;
    }
}
