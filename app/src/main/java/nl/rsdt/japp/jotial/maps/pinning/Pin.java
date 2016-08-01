package nl.rsdt.japp.jotial.maps.pinning;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-7-2016
 * Description...
 */
public class Pin implements Parcelable
{
    private MarkerOptions marker = new MarkerOptions();

    protected Pin()
    {

    }

    protected Pin(Parcel in) {
        marker = in.readParcelable(MarkerOptions.class.getClassLoader());
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(marker, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pin> CREATOR = new Creator<Pin>() {
        @Override
        public Pin createFromParcel(Parcel in) {
            return new Pin(in);
        }

        @Override
        public Pin[] newArray(int size) {
            return new Pin[size];
        }
    };

    public String getTitle() { return marker.getTitle(); }

    public String getSnippet() { return marker.getSnippet(); }

    public LatLng getLatLng() { return marker.getPosition(); }

    public void addToGoogleMap(GoogleMap googleMap)
    {

    }


    public static class Builder
    {
        Pin buffer = new Pin();

        public Builder setTitle(String title)
        {
            buffer.marker.title(title);
            return this;
        }

        public Builder setSnippet(String snippet)
        {
            buffer.marker.title(snippet);
            return this;
        }

        public Builder setPosition(LatLng latLng)
        {
            buffer.marker.position(latLng);
            return this;
        }

        public Pin create()
        {
            return buffer;
        }
    }
}
