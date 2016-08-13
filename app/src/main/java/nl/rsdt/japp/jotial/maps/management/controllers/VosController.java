package nl.rsdt.japp.jotial.maps.management.controllers;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rsdt.anl.RequestPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.data.structures.area348.VosInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class VosController extends MapItemController<VosInfo> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(getBundleId()))
            {
                this.items = savedInstanceState.getParcelableArrayList(getBundleId());
                AbstractTransducerResult<VosInfo> result = getTransducer().generate(items, VosInfo.class);
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
        saveInstanceState.putParcelableArrayList(getBundleId(), items);
    }

    @Override
    public AbstractTransducer<VosInfo> getTransducer() {
        return new VosTransducer(getStorageId(), getBundleId());
    }


    public ArrayList<BaseInfo> searchFor(String query) {
        ArrayList<BaseInfo> results = new ArrayList<>();
        VosInfo info;
        for(int i = 0; i < items.size(); i++) {
            info = items.get(i);
            if(info != null) {

                String current;
                String[] items = new String[] { info.opmerking, info.extra };
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
        ArrayList<String> entries = new ArrayList<>();
        VosInfo info;
        for(int i = 0; i < items.size(); i++) {
            info = items.get(i);
            if(info != null) {
                entries.add(info.opmerking);
                entries.add(info.extra);
            }
        }
        return entries;
    }


    public static class VosTransducer extends AbstractTransducer<VosInfo>
    {
        private String storageId;

        private String bundleId;

        public VosTransducer(String storageId, String bundleId) {
            this.storageId = storageId;
            this.bundleId = bundleId;
        }

        @Override
        public VosInfo[] retrieveFromStorage() {
            return AppData.getObject(storageId, VosInfo[].class);
        }

        @Override
        public VosInfo[] extract(String data) {
            return VosInfo.fromJsonArray(data);
        }

        @Override
        public AbstractTransducerResult<VosInfo> generate(VosInfo[] items) {
            if(items == null || items.length < 1) return new VosTransducerResult();

            VosTransducerResult result = new VosTransducerResult();
            result.setBundleId(bundleId);
            result.addItems(Arrays.asList(items));

            if(saveEnabled)
            {
                /**
                 * Saves the generated data.
                 * */
                AppData.saveObjectAsJson(items, storageId);
            }

            PolylineOptions pOptions = new PolylineOptions();
            pOptions.color(items[0].getAssociatedColor());
            pOptions.width(5);

            VosInfo current;

            for(int i = 0; i < items.length; i++)
            {
                current = items[i];

                MarkerOptions mOptions = new MarkerOptions();
                mOptions.title(String.valueOf(current.id));
                mOptions.anchor(0.5f, 0.5f);
                mOptions.position(current.getLatLng());
                mOptions.icon(BitmapDescriptorFactory.fromResource(current.getAssociatedDrawable()));
                result.add(mOptions);


                pOptions.add(current.getLatLng());
            }
            result.add(pOptions);

            return result;
        }

        public static class VosTransducerResult extends AbstractTransducerResult<VosInfo>
        {
            public VosTransducerResult() {};

            /**
             * Reconstructs the result.
             *
             * @param in The parcel where the result was written to
             */
            protected VosTransducerResult(Parcel in) {
                super(in);
                items = in.createTypedArrayList(VosInfo.CREATOR);
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeTypedList(items);
            }

            public static final Creator<VosTransducerResult> CREATOR = new Creator<VosTransducerResult>() {
                @Override
                public VosTransducerResult createFromParcel(Parcel in) {
                    return new VosTransducerResult(in);
                }

                @Override
                public VosTransducerResult[] newArray(int size) {
                    return new VosTransducerResult[size];
                }
            };
        }

    }

}
