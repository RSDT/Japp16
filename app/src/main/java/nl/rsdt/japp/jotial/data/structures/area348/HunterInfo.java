package nl.rsdt.japp.jotial.data.structures.area348;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.util.Map;

import nl.rsdt.japp.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the HunterInfo.
 */
public class HunterInfo extends BaseInfo implements Parcelable {

    /**
     * Initializes a new instance of HunterInfo from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     */
    protected HunterInfo(Parcel in) {
        super(in);
        datetime = in.readString();
        hunter = in.readString();
        icon = in.readInt();
    }

    /**
     * The dateTime the HunterInfo was created.
     */
    public String datetime;

    public String getDatetime() {
        return datetime;
    }

    /**
     * The user of the HunterInfo.
     */
    public String hunter;

    public String getHunter() {
        return hunter;
    }

    /**
     * The icon of the HunterInfo.
     * */
    public int icon;

    public int getIcon() {
        return icon;
    }

    public int getAssociatedDrawable()  {
        return getAssociatedDrawable(icon);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(datetime);
        dest.writeString(hunter);
        dest.writeInt(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HunterInfo> CREATOR = new Creator<HunterInfo>() {
        @Override
        public HunterInfo createFromParcel(Parcel in) {
            return new HunterInfo(in);
        }

        @Override
        public HunterInfo[] newArray(int size) {
            return new HunterInfo[size];
        }
    };


    public static int getAssociatedDrawable(int icon) {
        switch (icon) {
            case 0:
                return(R.drawable.hunter_0);
            case 1:
                return(R.drawable.hunter_1);
            case 2:
                return(R.drawable.hunter_2);
            case 3:
                return(R.drawable.hunter_3);
            case 4:
                return(R.drawable.hunter_4);
            case 5:
                return(R.drawable.hunter_5);
            case 6:
                return(R.drawable.hunter_6);
            case 7:
                return(R.drawable.hunter_7);
            case 8:
                return(R.drawable.hunter_8);
            case 9:
                return(R.drawable.hunter_9);
            case 10:
                return(R.drawable.hunter_10);
            case 11:
                return(R.drawable.hunter_11);
            case 12:
                return(R.drawable.hunter_12);
            case 13:
                return(R.drawable.hunter_13);
            case 14:
                return(R.drawable.hunter_14);
            case 15:
                return(R.drawable.hunter_15);
            default:
                return(R.drawable.hunter_0);
        }
    }

    /**
     * Deserializes a HunterInfo from JSON.
     *
     * @param json The JSON where the HunterInfo should be deserialized from.
     * @return A HunterInfo.
     */
    @Nullable
    public static HunterInfo fromJson(String json) {
        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
            jsonReader.setLenient(true);
            return new Gson().fromJson(jsonReader, HunterInfo.class);
        } catch(JsonParseException e)
        {
            Log.e("HunterInfo", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Deserializes a array of HunterInfo from JSON.
     *
     * @param json The JSON where the array of HunterInfo should be deserialized from.
     * @return A array of HunterInfo.
     */
    @Nullable
    public static HunterInfo[] fromJsonArray(String json) {
        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
            jsonReader.setLenient(true);
            return new Gson().fromJson(jsonReader, HunterInfo[].class);
        } catch(JsonParseException e)
        {
            Log.e("HunterInfo", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Deserializes a 2D array of HunterInfo from JSON.
     *
     * @param json The JSON where the 2D array of HunterInfo should be deserialized from.
     * @return A 2D array of HunterInfo.
     */
    @Nullable
    public static HunterInfo[][] formJsonArray2D(String json) {
        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
            jsonReader.setLenient(true);
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(jsonReader);
            HunterInfo[][] buffer = new HunterInfo[object.entrySet().size()][];
            int count = 0;
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                buffer[count] = fromJsonArray(entry.getValue().toString());
                count++;
            }
            return buffer;
        } catch(JsonParseException e)
        {
            Log.e("HunterInfo", e.getMessage(), e);
        }
        return null;
    }
}
