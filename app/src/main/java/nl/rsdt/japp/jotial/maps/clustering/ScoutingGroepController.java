package nl.rsdt.japp.jotial.maps.clustering;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.IntentCreatable;
import nl.rsdt.japp.jotial.Recreatable;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.clustering.osm.OsmScoutingGroepClusterManager;
import nl.rsdt.japp.jotial.maps.management.MapItemUpdatable;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.google.GoogleJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap;
import nl.rsdt.japp.jotial.net.apis.ScoutingGroepApi;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
public class ScoutingGroepController implements Recreatable, IntentCreatable, MapItemUpdatable, Callback<ArrayList<ScoutingGroepInfo>> {

    public static final String TAG = "ScoutingGroepController";

    public static final String STORAGE_ID = "SC";

    public static final String BUNDLE_ID = "SC";

    protected ClusterManagerInterface clusterManager;

    @Nullable
    public ClusterManagerInterface getClusterManager() {
        return clusterManager;
    }

    ArrayList<ScoutingGroepInfo> buffer = new ArrayList<>();

    @Override
    public void onIntentCreate(Bundle bundle) {
        if(bundle != null) {
            if(bundle.containsKey(BUNDLE_ID)) {
                buffer = bundle.getParcelableArrayList(BUNDLE_ID);
                if(clusterManager != null) {
                    clusterManager.addItems(buffer);
                    clusterManager.cluster();
                    buffer = null;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(BUNDLE_ID)) {
                ArrayList<ScoutingGroepInfo> items = savedInstanceState.getParcelableArrayList(BUNDLE_ID);
                if(items != null && !items.isEmpty()) {
                    if(clusterManager != null) {
                        clusterManager.addItems(items);
                        clusterManager.cluster();
                    } else {
                        buffer = items;
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        if(saveInstanceState != null) {
            if(clusterManager != null) {
                Collection<ScoutingGroepInfo> items = clusterManager.getItems();
                if(items != null) {
                    if(items instanceof ArrayList) {
                        saveInstanceState.putParcelableArrayList(BUNDLE_ID, (ArrayList<ScoutingGroepInfo>)items);
                    } else {
                        saveInstanceState.putParcelableArrayList(BUNDLE_ID, new ArrayList<>(items));
                    }
                }
            }
        }
    }

    public void onMapReady(IJotiMap jotiMap) {
        if (jotiMap instanceof GoogleJotiMap) {
            GoogleJotiMap googleJotiMap = (GoogleJotiMap) jotiMap;
            clusterManager = new ScoutingGroepClusterManager(Japp.getInstance(), googleJotiMap.getGoogleMap());
        } else if (jotiMap instanceof OsmJotiMap) {
            clusterManager = new OsmScoutingGroepClusterManager((OsmJotiMap) jotiMap);
        }else {
            clusterManager = new NoneClusterManager();
        }
        if(buffer != null) {
            clusterManager.addItems(buffer);
            clusterManager.cluster();
            buffer = null;
        }
    }

    @Override
    public void onResponse(Call<ArrayList<ScoutingGroepInfo>> call, Response<ArrayList<ScoutingGroepInfo>> response) {
        switch (response.code()) {
            case 200:
                if(clusterManager != null) {
                    if(clusterManager.getItems().isEmpty()) {
                        clusterManager.addItems(response.body());
                    } else {
                        clusterManager.clearItems();
                        clusterManager.addItems(response.body());
                    }

                } else {
                    buffer = response.body();
                }
                AppData.saveObjectAsJsonInBackground(response.body(), STORAGE_ID);
                break;

        }
    }

    @Override
    public void onFailure(Call<ArrayList<ScoutingGroepInfo>> call, Throwable t) {
        Log.e(TAG, t.toString(), t);
    }


    @Override
    public Call<ArrayList<ScoutingGroepInfo>> update(String mode) {
        ScoutingGroepApi api = Japp.getApi(ScoutingGroepApi.class);
        switch (mode) {
            case MODE_ALL:
                return api.getAll(JappPreferences.getAccountKey());
            case MODE_LATEST:
                return api.getAll(JappPreferences.getAccountKey());
        }
        return null;
    }

    @Override
    public void onUpdateInvoked() {
        Call<ArrayList<ScoutingGroepInfo>> call = update(MODE_ALL);
        if(call != null) {
            call.enqueue(this);
        }

    }

    @Override
    public void onUpdateMessage(UpdateInfo info) {
        Call<ArrayList<ScoutingGroepInfo>> call;
        switch (info.type) {
            case UpdateInfo.ACTION_NEW:
                 call = update(MODE_ALL);
                break;
            case UpdateInfo.ACTION_UPDATE:
                call = update(MODE_ALL);
                break;
            default:
                call = null;
                break;
        }
        if(call != null) {
            call.enqueue(this);
        }
    }

    public static void loadAndPutToBundle(Bundle bundle) {
        ArrayList<ScoutingGroepInfo> list = AppData.getObject(STORAGE_ID, new TypeToken<ArrayList<ScoutingGroepInfo>>(){}.getType());
        if(list != null && !list.isEmpty()) {
            bundle.putParcelableArrayList(BUNDLE_ID, list);
        }
    }

}
