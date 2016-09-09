package nl.rsdt.japp.jotial.maps.management.controllers;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.net.apis.HunterApi;
import retrofit2.Call;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class HunterController extends MapItemController<HashMap<String, ArrayList<HunterInfo>>, HunterController.HunterTransducer.Result> {

    public static final String CONTROLLER_ID = "HunterOpdrachtController";

    public static final String STORAGE_ID = "STORAGE_HUNTER";

    public static final String BUNDLE_ID = "HUNTER";

    public static final String BUNDLE_COUNT = "HUNTER_COUNT";

    /**
     * Handler for updating the Hunters.
     * */
    private Handler handler = new Handler();

    private HunterUpdateRunnable runnable;

    protected HashMap<String, ArrayList<HunterInfo>> data = new HashMap<>();

    public HunterController() {
        runnable = new HunterUpdateRunnable();
        handler.postDelayed(runnable, (long)JappPreferences.getHunterUpdateIntervalInMs());
    }

    @Override
    public String getId() {
        return CONTROLLER_ID;
    }

    @Override
    public String getStorageId() {
        return STORAGE_ID;
    }

    @Override
    public String getBundleId() {
        return BUNDLE_ID;
    }

    @Override
    public void onIntentCreate(Bundle bundle) {
        super.onIntentCreate(bundle);
        if(bundle != null) {
            HunterTransducer.Result result = bundle.getParcelable(BUNDLE_ID);
            if(result != null) {
                data = result.data;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(BUNDLE_COUNT)) {
                ArrayList<String> keys = savedInstanceState.getStringArrayList(BUNDLE_COUNT);
                if(keys != null) {
                    String key;
                    for(int i = 0; i < keys.size(); i++) {
                        key = keys.get(i);
                        ArrayList<HunterInfo> list = savedInstanceState.getParcelableArrayList(key);
                        if(list != null && !list.isEmpty()) {
                            data.put(key, list);
                        }
                    }
                    HunterTransducer.Result result = getTransducer().generate(data);
                    if(googleMap != null) {
                        processResult(result);
                    } else {
                        buffer = result;
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        ArrayList<String> keys = new ArrayList<>();
        for(Map.Entry<String, ArrayList<HunterInfo>> entry : data.entrySet()) {
            saveInstanceState.putParcelableArrayList(entry.getKey(), entry.getValue());
            keys.add(entry.getKey());
        }
        saveInstanceState.putStringArrayList(BUNDLE_COUNT, keys);
    }

    @Override
    public Marker searchFor(String query) {
        Marker marker;
        for(int m = 0; m < markers.size(); m++) {
            marker = markers.get(m);

            if(marker != null) {
            }
        }
        return null;
    }


    @Override
    public List<String> provide() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public HunterTransducer getTransducer() {
        return new HunterTransducer();
    }

    @Override
    public Call<HashMap<String, ArrayList<HunterInfo>>> update(String mode) {
        String name = JappPreferences.getHuntname();
        if(name.isEmpty()) {
            name = JappPreferences.getAccountUsername();
        }

        HunterApi api = Japp.getApi(HunterApi.class);
        switch (mode){
            case MODE_ALL:
                return api.getAllExcept(JappPreferences.getAccountKey(), name);
            case MODE_LATEST:
                return api.getAllExcept(JappPreferences.getAccountKey(), name);
        }
        return null;
    }

    @Override
    public void merge(HunterTransducer.Result other) {
        clear();
        processResult(other);
    }

    @Override
    protected void processResult(HunterTransducer.Result result) {
        super.processResult(result);
        data = result.data;
    }

    @Override
    protected void clear() {
        super.clear();
        data.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(data != null) {
            /**
             * TODO: clearing the data might result in the data clearance in the saved instance state bundle
             * */
            data.clear();
            data = null;
        }

        if(handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    public static class HunterTransducer extends AbstractTransducer<HashMap<String, ArrayList<HunterInfo>>, HunterTransducer.Result> {

        @Override
        public HashMap<String, ArrayList<HunterInfo>> load() {
            return AppData.getObject(STORAGE_ID, new TypeToken<HashMap<String, ArrayList<HunterInfo>>>(){}.getType());
        }

        @Override
        public void transduceToBundle(Bundle bundle) {
            bundle.putParcelable(BUNDLE_ID, generate(load()));
        }

        @Override
        public Result generate(HashMap<String, ArrayList<HunterInfo>> data) {
            if(data == null || data.isEmpty()) return new Result();

            Result result = new Result();
            result.setBundleId(BUNDLE_ID);
            result.data = data;

            if(saveEnabled)
            {
                AppData.saveObjectAsJson(data, STORAGE_ID);
            }

            ArrayList<HunterInfo> currentData;


            for(Map.Entry<String, ArrayList<HunterInfo>> entry : data.entrySet()) {
                currentData = entry.getValue();

                Collections.sort(currentData, new Comparator<HunterInfo>() {
                    @Override
                    public int compare(HunterInfo info1, HunterInfo info2) {
                        Date firstDate = null;
                        Date secondDate = null;
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
                            firstDate = format.parse(info1.getDatetime());
                            secondDate = format.parse(info2.getDatetime());
                        } catch (ParseException e) {
                            Log.e(TAG, e.toString(), e);
                        }
                        if(firstDate != null && secondDate != null) {
                            return firstDate.compareTo(secondDate);
                        }
                        return 0;
                    }
                });

                /**
                 * Setup the polyline for the Hunter.
                 * */
                PolylineOptions pOptions = new PolylineOptions();
                pOptions.width(5);
                pOptions.color(Color.BLACK);

                /**
                 * Loop through each info.
                 * */
                for (int i = 0; i < currentData.size(); i++) {
                    pOptions.add(currentData.get(i).getLatLng());
                }

                result.add(pOptions);

                /**
                 * The lastest info should be the first one in the array.
                 * */
                HunterInfo lastestInfo = currentData.get(currentData.size() - 1);

                /**
                 * Setup the marker for the Hunter.
                 * */
                MarkerOptions mOptions = new MarkerOptions();
                mOptions.title(String.valueOf(lastestInfo.id));
                mOptions.position(lastestInfo.getLatLng());
                mOptions.icon(BitmapDescriptorFactory.fromResource(lastestInfo.getAssociatedDrawable()));

                result.add(mOptions);

            }
            return result;

        }

        public static class Result extends AbstractTransducer.Result {

            protected HashMap<String, ArrayList<HunterInfo>> data = new HashMap<>();

            private Result() {};

            protected Result(Parcel in) {
                super(in);
                String[] keys = in.createStringArray();
                if(keys != null) {
                    String key;
                    for(int i = 0; i < keys.length; i++) {
                        key = keys[i];
                        ArrayList<HunterInfo> list = in.createTypedArrayList(HunterInfo.CREATOR);
                        if(list != null && !list.isEmpty()) {
                            data.put(key, list);
                        }
                    }
                }

            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);

                String[] keys = new String[data.size()];
                data.keySet().toArray(keys);
                dest.writeStringArray(keys);
                for(Map.Entry<String, ArrayList<HunterInfo>> entry : data.entrySet()) {
                    dest.writeTypedList(entry.getValue());
                }

            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<Result> CREATOR = new Creator<Result>() {
                @Override
                public Result createFromParcel(Parcel in) {
                    return new Result(in);
                }

                @Override
                public Result[] newArray(int size) {
                    return new Result[size];
                }
            };

        }

    }

    private class HunterUpdateRunnable implements Runnable {

        @Override
        public void run() {
            onUpdateInvoked();
            handler.postDelayed(this, (long)JappPreferences.getHunterUpdateIntervalInMs());
        }
    }

}
