package nl.rsdt.japp.jotial.maps.pinning;

import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
public class Pin {

    protected IMarker marker;

    protected Data data;

    public static Pin create(IJotiMap jotiMap, Data data) {
        Pin buffer = new Pin();

        MarkerIdentifier identifier = new MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_PIN)
                .add("title", data.title)
                .add("description", data.description)
                .add("icon", String.valueOf(data.icon))
                .create();

        buffer.marker = jotiMap.addMarker(new Pair<>(new MarkerOptions()
                .title(new Gson().toJson(identifier))
                .position(data.position), BitmapFactory.decodeResource(Japp.getInstance().getResources(), data.icon)));
        buffer.data = data;
        return buffer;
    }

    public static class Data implements Parcelable{

        protected String title;

        protected String description;

        protected LatLng position;

        protected int icon;

        public Data() {

        }

        public Data(String title, String description, LatLng position, int icon) {
            this.title = title;
            this.description = description;
            this.position = position;
            this.icon = icon;
        }

        protected Data(Parcel in) {
            title = in.readString();
            description = in.readString();
            position = in.readParcelable(LatLng.class.getClassLoader());
            icon = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(description);
            dest.writeParcelable(position, flags);
            dest.writeInt(icon);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };
    }

}
