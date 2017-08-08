package nl.rsdt.japp.jotial.maps.management.controllers;

import android.os.Bundle;
import android.os.Parcel;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.R;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.data.structures.area348.FotoOpdrachtInfo;

import nl.rsdt.japp.jotial.io.AppData;

import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.management.StandardMapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;

import nl.rsdt.japp.jotial.maps.wrapper.Marker;
import nl.rsdt.japp.jotial.net.apis.FotoApi;
import retrofit2.Call;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class FotoOpdrachtController extends StandardMapItemController<FotoOpdrachtInfo, FotoOpdrachtController.FotoOpdrachtTransducer.Result>{

    public static final String CONTROLLER_ID = "FotoOpdrachtController";

    public static final String STORAGE_ID = "STORAGE_FOTO";

    public static final String BUNDLE_ID = "FOTO";

    public static final String REQUEST_ID = "REQUEST_FOTO";

    public FotoOpdrachtController() {
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
    public Call<ArrayList<FotoOpdrachtInfo>> update(String mode) {
        FotoApi api = Japp.getApi(FotoApi.class);
        switch (mode) {
            case MODE_ALL:
                return api.getAll(JappPreferences.getAccountKey());
            case MODE_LATEST:
                return api.getAll(JappPreferences.getAccountKey());
        }
        return null;
    }

    @Override
    public Marker searchFor(String query) {
        ArrayList<BaseInfo> results = new ArrayList<>();
        FotoOpdrachtInfo info;
        for(int i = 0; i < items.size(); i++) {
            info = items.get(i);
            if(info != null) {

                String current;
                String[] items = new String[] { info.info, info.extra };
                for(int x = 0; x < items.length; x++) {
                    current = items[x];
                    if(current.toLowerCase(Locale.ROOT).startsWith(query)) results.add(info);
                }
            }
        }
        return null;
    }

    @Override
    public List<String> provide() {
        ArrayList<String> results = new ArrayList<>();
        FotoOpdrachtInfo info;
        for(int i = 0; i < items.size(); i++) {
            info = items.get(i);
            if(info != null) {
                results.add(info.info);
                results.add(info.extra);
            }
        }
        return results;
    }

    @Override
    public FotoOpdrachtTransducer getTransducer() {
        return new FotoOpdrachtTransducer();
    }


    public static class FotoOpdrachtTransducer extends AbstractTransducer<ArrayList<FotoOpdrachtInfo>, FotoOpdrachtTransducer.Result> {

        @Override
        public ArrayList<FotoOpdrachtInfo> load() {
            return AppData.getObject(STORAGE_ID, new TypeToken<ArrayList<FotoOpdrachtInfo>>() {}.getType());
        }

        @Override
        public void transduceToBundle(Bundle bundle) {
            bundle.putParcelable(BUNDLE_ID, generate(load()));
        }

        @Override
        public Result generate(ArrayList<FotoOpdrachtInfo> items) {
            if(items == null || items.isEmpty()) return new Result();

            Result result = new Result();
            result.setBundleId(BUNDLE_ID);
            result.addItems(items);

            if(saveEnabled) {
                AppData.saveObjectAsJson(items, STORAGE_ID);
            }


            FotoOpdrachtInfo info;

            /**
             * Loops through each FotoOpdrachtInfo.
             * */
            for (int i = 0; i < items.size(); i++) {
                info = items.get(i);
                if(info != null) {

                    MarkerIdentifier identifier  = new MarkerIdentifier.Builder()
                            .setType(MarkerIdentifier.TYPE_FOTO)
                            .add("info", info.info)
                            .add("extra", info.extra)
                            .add("icon", String.valueOf(info.getAssociatedDrawable()))
                            .create();

                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.anchor(0.5f, 0.5f);
                    mOptions.position(info.getLatLng());
                    mOptions.title(new Gson().toJson(identifier));

                    if(info.klaar == 1)
                    {
                        mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_20x20_klaar));
                    }
                    else
                    {
                        mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_20x20));
                    }
                    result.add(mOptions);
                }
            }
            return result;
        }

        /**
         * @author Dingenis Sieger Sinke
         * @version 1.0
         * @since 31-7-2016
         * Description...
         */
        public static class Result extends AbstractTransducer.StandardResult<FotoOpdrachtInfo> {

            public Result() {}

            /**
             * Reconstructs the result.
             *
             * @param in The parcel where the result was written to
             */
            protected Result(Parcel in) {
                super(in);
                items = in.createTypedArrayList(FotoOpdrachtInfo.CREATOR);
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeTypedList(items);
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

}
