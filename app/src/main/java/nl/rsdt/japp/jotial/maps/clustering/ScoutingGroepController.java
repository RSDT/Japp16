package nl.rsdt.japp.jotial.maps.clustering;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.activities.MainActivity;
import nl.rsdt.japp.jotial.IntentCreatable;
import nl.rsdt.japp.jotial.Recreatable;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.management.MapItemUpdatable;
import nl.rsdt.japp.jotial.net.ApiGetJsonArrayRequest;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
public class ScoutingGroepController implements OnMapReadyCallback, Recreatable, IntentCreatable, MapItemUpdatable, Response.Listener<JSONArray>, Response.ErrorListener {

    public static final String TAG = "ScoutingGroepController";

    public static final String STORAGE_ID = "SC";

    public static final String BUNDLE_ID = "SC";

    ScoutingGroepClusterManager clusterManager;

    @Nullable
    public ScoutingGroepClusterManager getClusterManager() {
        return clusterManager;
    }

    ArrayList<ScoutingGroepInfo> buffer = new ArrayList<>();

    @Override
    public void onIntentCreate(Bundle bundle) {
        if(bundle != null) {
            buffer = bundle.getParcelableArrayList(BUNDLE_ID);
            if(clusterManager != null) {
                clusterManager.addItems(buffer);
                clusterManager.cluster();
                buffer = null;
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
        Collection<ScoutingGroepInfo> items = clusterManager.getItems();
        if(items != null) {
            if(items instanceof ArrayList) {
                saveInstanceState.putParcelableArrayList(BUNDLE_ID, (ArrayList<ScoutingGroepInfo>)items);
            } else {
                saveInstanceState.putParcelableArrayList(BUNDLE_ID, new ArrayList<>(items));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        clusterManager = new ScoutingGroepClusterManager(Japp.getInstance(), googleMap);

        if(buffer != null) {
            clusterManager.addItems(buffer);
            clusterManager.cluster();
            buffer = null;
        }
    }

    @Override
    public String getUrlByAssociatedMode(String mode) {
        switch (mode) {
            case MODE_ALL:
                return new ApiUrlBuilder().append("sc").append("all").buildAsString();
            case MODE_LATEST:
                return new ApiUrlBuilder().append("sc").append("all").buildAsString();
            default:
                return null;
        }
    }

    @Override
    public void onUpdateInvoked(RequestQueue queue) {
        Request request = new ApiGetJsonArrayRequest(getUrlByAssociatedMode(MODE_ALL), this, this);
        request.setTag(MainActivity.TAG);
        queue.add(request);
    }

    @Override
    public void onUpdateMessage(RequestQueue queue, UpdateInfo info) {
        Request request;
        String mode;
        switch (info.action) {
            case UpdateInfo.ACTION_NEW:
                mode = MODE_LATEST;
                break;
            case UpdateInfo.ACTION_UPDATE:
                mode = MODE_ALL;
                break;
            default:
                mode = null;
                break;
        }

        if(mode != null) {
            request = new ApiGetJsonArrayRequest(getUrlByAssociatedMode(mode), this, this);
            request.setTag(MainActivity.TAG);
            queue.add(request);
        }
    }

    @Override
    public void onResponse(JSONArray response) {
        ArrayList<ScoutingGroepInfo> list = new Gson().fromJson(response.toString(), new TypeToken<ArrayList<ScoutingGroepInfo>>(){}.getType());
        if(clusterManager != null) {
            clusterManager.addItems(list);
        } else {
            buffer = list;
        }
        AppData.saveObjectAsJsonInBackground(list, STORAGE_ID);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, error.toString(), error);
    }

}
