package nl.rsdt.japp.jotial.maps.management.controllers;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rsdt.anl.RequestPool;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class HunterController extends MapItemController<HunterInfo[]> {

    public static final String CONTROLLER_ID = "HunterOpdrachtController";

    public static final String STORAGE_ID = "STORAGE_HUNTER";

    public static final String BUNDLE_ID = "HUNTER";

    public static final String BUNDLE_COUNT = "HUNTER_COUNT";

    public static final String REQUEST_ID = "REQUEST_HUNTER";

    public HunterController() {
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
            if(savedInstanceState.containsKey(BUNDLE_COUNT))
            {
                AbstractTransducer<HunterInfo[]> transducer = getTransducer();
                AbstractTransducerResult<HunterInfo[]> result;
                int size = savedInstanceState.getInt(BUNDLE_COUNT);
                for(int i = 0; i < size; i++)
                {
                    HunterInfo[] item = (HunterInfo[]) savedInstanceState.getParcelableArray(BUNDLE_ID + i);
                    if(item != null)
                    {
                        this.items.add(item);
                    }
                }
                result = transducer.generate(items, HunterInfo[].class);
                if(googleMap != null)
                {
                    processResult(result);
                }
                else
                {
                    buffer = result;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        HunterInfo[] item;
        for(int i = 0; i < items.size(); i++)
        {
            item = items.get(i);
            if(item != null)
            {
                saveInstanceState.putParcelableArray(BUNDLE_ID + i, item);
            }
        }
        saveInstanceState.putInt(BUNDLE_COUNT, items.size());
    }


    @Override
    public void onUpdateInvoked(RequestPool pool, boolean userInvoked) {
        if(userInvoked) {
            pool.query(new WebRequest.Builder()
                    .setId(REQUEST_ID)
                    .setMethod(WebRequestMethod.GET)
                    .setUrl(new ApiUrlBuilder().append("hunter").append("all").build())
                    .create());
        } else if(items.isEmpty() || isElapsedSinceLastUpdate(30000)) {
            pool.query(new WebRequest.Builder()
                    .setId(REQUEST_ID)
                    .setMethod(WebRequestMethod.GET)
                    .setUrl(new ApiUrlBuilder().append("hunter").append("all").build())
                    .create());
        }
    }

    @Override
    public ArrayList<BaseInfo> searchFor(String query) {
        ArrayList<BaseInfo> results= new ArrayList<>();
        HunterInfo info;
        HunterInfo[] array;
        for(int i = 0; i < items.size(); i++) {
            array = items.get(i);
            if(array != null && array.length > 0) {
                info = array[0];
                if(info.hunter.toLowerCase(Locale.ROOT).startsWith(query)) results.add(info);
            }
        }
        return results;
    }

    @Override
    public List<String> getEntries() {
        ArrayList<String> entries = new ArrayList<>();
        HunterInfo info;
        HunterInfo[] array;
        for(int i = 0; i < items.size(); i++) {
            array = items.get(i);
            if(array != null && array.length > 0) {
                info = array[0];
                entries.add(info.hunter);
            }
        }
        return entries;
    }

    @Override
    public AbstractTransducer<HunterInfo[]> getTransducer() {
        return new HunterTransducer();
    }

    public static class HunterTransducer extends AbstractTransducer<HunterInfo[]> {

        @Override
        public HunterInfo[][] retrieveFromStorage() {
            return AppData.getObject(STORAGE_ID, HunterInfo[][].class);
        }

        @Override
        public HunterInfo[][] extract(String data) {
            return HunterInfo.formJsonArray2D(data);
        }

        @Override
        public AbstractTransducerResult<HunterInfo[]> generate(HunterInfo[][] items) {
            if(items == null || items.length < 1) return new HunterTransducerResult();

            HunterTransducerResult result = new HunterTransducerResult();
            result.setBundleId(BUNDLE_ID);
            result.addItems(Arrays.asList(items));

            if(saveEnabled)
            {
                AppData.saveObjectAsJson(items, STORAGE_ID);
            }

            /**
             * Loop through each user.
             * */
            for (int h = 0; h < items.length; h++) {
                /**
                 * Setup the polyline for the Hunter.
                 * */
                PolylineOptions pOptions = new PolylineOptions();
                pOptions.width(5);
                pOptions.color(Color.BLACK);

                /**
                 * Loop through each info.
                 * */
                for (int i = 0; i < items[h].length; i++) {
                    pOptions.add(items[h][i].getLatLng());
                }

                result.add(pOptions);

                /**
                 * The lastest info should be the first one in the array.
                 * */
                HunterInfo lastestInfo = items[h][0];

                /**
                 * Setup the marker for the Hunter.
                 * TODO: import hunter icons
                 * */
                MarkerOptions mOptions = new MarkerOptions();
                mOptions.title(String.valueOf(lastestInfo.id));
                mOptions.position(lastestInfo.getLatLng());
                //mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunter));

                result.add(mOptions);

            }
            return result;
        }

        public static class HunterTransducerResult extends AbstractTransducerResult<HunterInfo[]> {

            private HunterTransducerResult() {};

            protected HunterTransducerResult(Parcel in) {
                super(in);

                int size = in.readInt();
                for(int i = 0; i < size; i++)
                {
                    HunterInfo[] array = in.createTypedArray(HunterInfo.CREATOR);
                    items.add(array);
                }
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);

                dest.writeInt(items.size());

                HunterInfo[] current;
                for(int i = 0; i < items.size(); i++)
                {
                    current = items.get(i);
                    if(current != null)
                    {
                        dest.writeTypedArray(current, 0);
                    }
                }
            }

            public static final Creator<HunterTransducerResult> CREATOR = new Creator<HunterTransducerResult>() {
                @Override
                public HunterTransducerResult createFromParcel(Parcel in) {
                    return new HunterTransducerResult(in);
                }

                @Override
                public HunterTransducerResult[] newArray(int size) {
                    return new HunterTransducerResult[size];
                }
            };

        }

    }

}
