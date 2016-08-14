package nl.rsdt.japp.jotial.data.structures.area348;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the FotoOpdrachtInfo.
 */
public class FotoOpdrachtInfo extends BaseInfo implements Parcelable {

    /**
     * The name of the FotoOpdrachtInfo.
     */
    public int foto_nr;

    /**
     * The info of the FotoOpdrachtInfo.
     */
    public String info;

    /**
     * The extra of the FotoOpdrachtInfo.
     */
    public String extra;

    /**
     * The value indicating if FotoOpdrachtInfo is completed or not.
     */
    public int klaar;

    protected FotoOpdrachtInfo(Parcel in) {
        super(in);
        foto_nr = in.readInt();
        info = in.readString();
        extra = in.readString();
        klaar = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(foto_nr);
        dest.writeString(info);
        dest.writeString(extra);
        dest.writeInt(klaar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FotoOpdrachtInfo> CREATOR = new Creator<FotoOpdrachtInfo>() {
        @Override
        public FotoOpdrachtInfo createFromParcel(Parcel in) {
            return new FotoOpdrachtInfo(in);
        }

        @Override
        public FotoOpdrachtInfo[] newArray(int size) {
            return new FotoOpdrachtInfo[size];
        }
    };

    /**
     * Deserializes a FotoOpdrachtInfo from the JSON.
     *
     * @param json The JSON where the FotoOpdrachtInfo should be deserialized from.
     * @return A FotoOpdrachtInfo.
     */
    @Nullable
    public static FotoOpdrachtInfo fromJson(String json) {
        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
            jsonReader.setLenient(true);
            return new Gson().fromJson(jsonReader, FotoOpdrachtInfo.class);
        } catch(JsonParseException e)
        {
            Log.e("FotoOpdrachtInfo", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Deserializes a array of FotoOpdrachtInfo from the JSON.
     *
     * @param json The JSON where the array of FotoOpdrachtInfo should be deserialized from.
     * @return A array of FotoOpdrachtInfo.
     */
    @Nullable
    public static FotoOpdrachtInfo[] fromJsonArray(String json) {
        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
            jsonReader.setLenient(true);
            return new Gson().fromJson(jsonReader, FotoOpdrachtInfo[].class);
        } catch(JsonParseException e)
        {
            Log.e("FotoOpdrachtInfo", e.getMessage(), e);
        }
        return null;
    }

}
