package nl.rsdt.japp.jotial.maps.management.transformation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import nl.rsdt.japp.jotial.BundleIdentifiable;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class AbstractTransducerResult<T> implements Parcelable, BundleIdentifiable {

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
     * The list of items.
     */
    protected ArrayList<T> items = new ArrayList<>();

    /**
     * Adds items to the list.
     */
    public void addItems(List<T> items) {
        this.items.addAll(items);
    }

    /**
     * Gets the items.
     */
    public ArrayList<T> getItems() {
        return items;
    }

    /**
     * The Marker list.
     */
    protected ArrayList<MarkerOptions> markers;

    /**
     * Adds a MarkerOptions object to the markers list.
     */
    public void add(MarkerOptions options) {
        markers.add(options);
    }

    public ArrayList<MarkerOptions> getMarkers() {
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
     * Empty constructor
     * */
    protected AbstractTransducerResult() {
        markers = new ArrayList<>();
        polylines = new ArrayList<>();
        polygons = new ArrayList<>();
    };

    /**
     * Reconstructs the result.
     *
     * @param in The parcel where the result was written to
     */
    protected AbstractTransducerResult(Parcel in) {
        bundleId = in.readString();
        markers = in.createTypedArrayList(MarkerOptions.CREATOR);
        polylines = in.createTypedArrayList(PolylineOptions.CREATOR);
        polygons = in.createTypedArrayList(PolygonOptions.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bundleId);
        dest.writeTypedList(markers);
        dest.writeTypedList(polylines);
        dest.writeTypedList(polygons);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AbstractTransducerResult> CREATOR = new Creator<AbstractTransducerResult>() {
        @Override
        public AbstractTransducerResult createFromParcel(Parcel in) {
            return new AbstractTransducerResult(in) {};
        }

        @Override
        public AbstractTransducerResult[] newArray(int size) {
            return new AbstractTransducerResult[size];
        }
    };

}
