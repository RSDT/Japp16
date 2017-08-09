package nl.rsdt.japp.jotial.maps.management.transformation;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import nl.rsdt.japp.jotial.BundleIdentifiable;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransduceTask;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class AbstractTransducer<I, O extends AbstractTransducer.Result> {

    protected boolean saveEnabled = true;

    public boolean isSaveEnabled() {
        return saveEnabled;
    }

    public void setSaveEnabled(boolean saveEnabled) {
        this.saveEnabled = saveEnabled;
    }

    public abstract I load();

    public abstract O generate(I data);

    public abstract void transduceToBundle(Bundle bundle);

    public void enqueue(I data, AsyncTransduceTask.OnTransduceCompletedCallback<O> callback) {
        AsyncTransduceTask<I, O> task = new AsyncTransduceTask<>();
        task.setTransducer(this);
        task.setData(data);
        task.setCallback(callback);
        task.execute();
    }

    public abstract static class Result implements Parcelable, BundleIdentifiable {

        /**
         * The id of the result.
         */
        protected String bundleId;

        /**
         * Gets the id of the result.
         */
        @Override
        public String getBundleId() {
            return bundleId;
        }

        /**
         * Sets the id of the result.
         * */
        public void setBundleId(String id) {
            this.bundleId = id;
        }

        /**
         * The Marker list.
         */
        protected ArrayList<Pair<MarkerOptions,Bitmap>> markers;

        /**
         * Adds a MarkerOptions object to the markers list.
         */
        public void add(Pair<MarkerOptions,Bitmap> options) {
            markers.add(options);
        }

        public ArrayList<Pair<MarkerOptions,Bitmap>> getMarkers() {
            return markers;
        }

        /**
         * The Polyline list.
         */
        protected ArrayList<PolylineOptions> polylines;

        /**
         * Adds a PolylineOptions object to the polyline list.
         */
        public void add(PolylineOptions options) {
            polylines.add(options);
        }

        public ArrayList<PolylineOptions> getPolylines() {
            return polylines;
        }

        /**
         * The Polygon list.
         */
        protected ArrayList<PolygonOptions> polygons;

        /**
         * Adds a PolygonOptions object to the polygon list.
         */
        public void add(PolygonOptions options) {
            polygons.add(options);
        }

        public ArrayList<PolygonOptions> getPolygons() {
            return polygons;
        }

        /**
         * The Circle list.
         */
        protected ArrayList<CircleOptions> circles;

        /**
         * Adds a CircleOptions object to the polygon list.
         */
        public void add(CircleOptions options) { circles.add(options); }

        /**
         * Gets the circles.
         * */
        public ArrayList<CircleOptions> getCircles() {
            return circles;
        }

        protected Result() {
            markers = new ArrayList<>();
            polylines = new ArrayList<>();
            polygons = new ArrayList<>();
            circles = new ArrayList<>();
        }

        /**
         * Reconstructs the result.
         *
         * @param in The parcel where the result was written to
         */
        protected Result(Parcel in) {
            bundleId = in.readString();
            markers = new ArrayList<>();
            ArrayList<MarkerOptions> optionsList = in.createTypedArrayList(MarkerOptions.CREATOR);
            ArrayList<Bitmap> bitmapList = in.createTypedArrayList(Bitmap.CREATOR);
            ArrayList<Pair<MarkerOptions, Bitmap>> markers = new ArrayList<>();

            polylines = in.createTypedArrayList(PolylineOptions.CREATOR);
            polygons = in.createTypedArrayList(PolygonOptions.CREATOR);
            circles = in.createTypedArrayList(CircleOptions.CREATOR);
            if (bitmapList.size() != optionsList.size()){
                throw new RuntimeException("optionlist and bitmapList are not equal in size");
            }
            for (int i = 0; i < optionsList.size(); i++){
                markers.add(new Pair<MarkerOptions, Bitmap>(optionsList.get(i),bitmapList.get(i)));
            }
            this.markers = markers;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(bundleId);
            ArrayList<MarkerOptions> optionsList = new ArrayList<>();
            ArrayList<Bitmap> bitmapList = new ArrayList<>();
            for (Pair<MarkerOptions,Bitmap> p : markers){
                optionsList.add(p.first);
                bitmapList.add(p.second);
            }

            dest.writeTypedList(optionsList);
            dest.writeTypedList(bitmapList);
            dest.writeTypedList(polylines);
            dest.writeTypedList(polygons);
            dest.writeTypedList(circles);
        }
    }

    public abstract static class StandardResult<T extends Parcelable> extends Result {

        protected ArrayList<T> items = new ArrayList<>();

        public StandardResult() {}

        public StandardResult(Parcel in) {
            super(in);
        }

        public void addItems(List<T> items) {
            this.items.addAll(items);
        }

        public ArrayList<T> getItems() {
            return items;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StandardResult> CREATOR = new Creator<StandardResult>() {
            @Override
            public StandardResult createFromParcel(Parcel in) {
                return new StandardResult(){};
            }

            @Override
            public StandardResult[] newArray(int size) {
                return new StandardResult[size];
            }
        };


    }

}
