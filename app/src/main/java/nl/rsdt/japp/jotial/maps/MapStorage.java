package nl.rsdt.japp.jotial.maps;

import android.os.Bundle;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepController;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncBundleTransduceTask;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 19-9-2017
 * Description...
 */

public class MapStorage implements AsyncBundleTransduceTask.OnBundleTransduceCompletedCallback   {

    public static final MapStorage instance = new MapStorage();

    public static MapStorage getInstance() {
        return instance;
    }

    private Bundle data = new Bundle();

    private ArrayList<OnMapDataLoadedCallback> callbacks = new ArrayList<>();

    public void add(OnMapDataLoadedCallback callback) {
        callbacks.add(callback);
    }

    public void remove(OnMapDataLoadedCallback callback) {
        callbacks.remove(callback);
    }

    public Bundle getData() {
        return data;
    }

    public void load() {
        /**
         * Load in the map data.
         * */
        new AsyncBundleTransduceTask(this).execute(MapItemController.getAll());
    }

    public void clear() {
        if(data != null) {
            data.clear();
            data = null;
        }
    }

    @Override
    public void onTransduceCompleted(Bundle bundle) {
        data = bundle;
        ScoutingGroepController.loadAndPutToBundle(data);
        for(OnMapDataLoadedCallback callback : callbacks) {
            if(callback != null) {
                callback.onMapDataLoaded();
            }
        }
    }

    public interface OnMapDataLoadedCallback {
        void onMapDataLoaded();
    }
}
