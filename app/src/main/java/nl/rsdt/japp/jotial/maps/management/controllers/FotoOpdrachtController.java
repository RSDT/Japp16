package nl.rsdt.japp.jotial.maps.management.controllers;

import android.os.Bundle;
import android.os.Parcel;

import com.android.internal.util.Predicate;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.rsdt.anl.RequestPool;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.R;

import nl.rsdt.japp.application.JappPreferences;

import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.data.structures.area348.FotoOpdrachtInfo;

import nl.rsdt.japp.jotial.data.structures.area348.VosInfo;
import nl.rsdt.japp.jotial.io.AppData;

import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;

import nl.rsdt.japp.jotial.maps.searching.SearchEntry;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class FotoOpdrachtController extends MapItemController<FotoOpdrachtInfo> {

    public static final String CONTROLLER_ID = "FotoOpdrachtController";

    public static final String STORAGE_ID = "STORAGE_FOTO";

    public static final String BUNDLE_ID = "FOTO";

    public static final String REQUEST_ID = "REQUEST_FOTO";

    public FotoOpdrachtController() {
        this.condtion = new Predicate<WebResponse>() {
            @Override
            public boolean apply(WebResponse response) {
                return response.getRequest().getId().equals(REQUEST_ID) &&
                        response.getResponseCode() == 200;
            }
        };
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
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(BUNDLE_ID))
            {
                this.items = savedInstanceState.getParcelableArrayList(BUNDLE_ID);
                AbstractTransducerResult<FotoOpdrachtInfo> result = getTransducer().generate(items, FotoOpdrachtInfo.class);
                if(googleMap != null) {
                    processResult(result);
                }
                else {
                    buffer = result;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        saveInstanceState.putParcelableArrayList(BUNDLE_ID, items);
    }

    @Override
    public void onUpdateInvoked(RequestPool pool, boolean userInvoked) {
        if(userInvoked) {
            pool.query(new WebRequest.Builder()
                    .setId(REQUEST_ID)
                    .setMethod(WebRequestMethod.GET)
                    .setUrl(new ApiUrlBuilder().append("foto").append("all").build())
                    .create());
        } else if(items.isEmpty() || isElapsedSinceLastUpdate(JappPreferences.getFotoIntervalRate()) ) {
            pool.query(new WebRequest.Builder()
                    .setId(REQUEST_ID)
                    .setMethod(WebRequestMethod.GET)
                    .setUrl(new ApiUrlBuilder().append("foto").append("all").build())
                    .create());
        }
    }

    public ArrayList<BaseInfo> searchFor(String query) {
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
        return results;
    }

    @Override
    public List<String> getEntries() {
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
    public AbstractTransducer<FotoOpdrachtInfo> getTransducer() {
        return new FotoOpdrachtTransducer();
    }

    public static class FotoOpdrachtTransducer extends AbstractTransducer<FotoOpdrachtInfo> {

        @Override
        public FotoOpdrachtInfo[] retrieveFromStorage() {
            return AppData.getObject(STORAGE_ID, FotoOpdrachtInfo[].class);
        }

        @Override
        public FotoOpdrachtInfo[] extract(String data) {
            return FotoOpdrachtInfo.fromJsonArray(data);
        }

        @Override
        public AbstractTransducerResult<FotoOpdrachtInfo> generate(FotoOpdrachtInfo[] items) {
            if(items == null || items.length < 1) return new FotoOpdrachtTransducerResult();

            FotoOpdrachtTransducerResult result = new FotoOpdrachtTransducerResult();
            result.setBundleId(BUNDLE_ID);
            result.addItems(Arrays.asList(items));

            if(saveEnabled) {
                AppData.saveObjectAsJson(items, STORAGE_ID);
            }

            /**
             * Loops through each FotoOpdrachtInfo.
             * */
            for (int i = 0; i < items.length; i++) {
                MarkerOptions mOptions = new MarkerOptions();
                mOptions.anchor(0.5f, 0.5f);
                mOptions.position(items[i].getLatLng());
                mOptions.title(String.valueOf(items[i].id));

                if(items[i].klaar)
                {
                    mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_20x20_klaar));
                }
                else
                {
                    mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_20x20));
                }
                result.add(mOptions);
            }
            return result;
        }

        /**
         * @author Dingenis Sieger Sinke
         * @version 1.0
         * @since 31-7-2016
         * Description...
         */
        public static class FotoOpdrachtTransducerResult extends AbstractTransducerResult<FotoOpdrachtInfo> {

            public FotoOpdrachtTransducerResult() {}

            /**
             * Reconstructs the result.
             *
             * @param in The parcel where the result was written to
             */
            protected FotoOpdrachtTransducerResult(Parcel in) {
                super(in);
                items = in.createTypedArrayList(FotoOpdrachtInfo.CREATOR);
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeTypedList(items);
            }

            public static final Creator<FotoOpdrachtTransducerResult> CREATOR = new Creator<FotoOpdrachtTransducerResult>() {
                @Override
                public FotoOpdrachtTransducerResult createFromParcel(Parcel in) {
                    return new FotoOpdrachtTransducerResult(in);
                }

                @Override
                public FotoOpdrachtTransducerResult[] newArray(int size) {
                    return new FotoOpdrachtTransducerResult[size];
                }
            };
        }

    }

}
