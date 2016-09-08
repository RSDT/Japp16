package nl.rsdt.japp.jotial.data.structures.area348;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the VosInfo.
 */
public class VosInfo extends BaseInfo implements Parcelable {

    /**
     * Initializes a new instance of VosInfo from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     */
    protected VosInfo(Parcel in) {
        super(in);
        datetime = in.readString();
        team = in.readString();
        teamName= in.readString();
        note = in.readString();
        extra = in.readString();
        hintNr = in.readInt();
        icon = in.readInt();
    }

    @SerializedName("datetime")
    /**
     * The dateTime the vosInfo was created.
     */
    private String datetime;

    public String getDatetime() {
        return datetime;
    }

    @SerializedName("team")
    /**
     * The team of the VosInfo as a char.
     */
    private String team;

    public String getTeam() {
        return team;
    }

    @SerializedName("team_naam")
    /**
     * The team of the VosInfo as a whole name.
     */
    private String teamName;

    public String getTeamName() {
        return teamName;
    }

    @SerializedName("opmerking")
    /**
     * A extra of the VosInfo.
     */
    private String note;

    public String getNote() {
        return note;
    }

    @SerializedName("extra")
    /**
     * The user of the VosInfo.
     */
    private String extra;

    public String getExtra() {
        return extra;
    }

    @SerializedName("hint_nr")
    /**
     * The hint number of the VosInfo.
     */
    private int hintNr;

    public int getHintNr() {
        return hintNr;
    }

    @SerializedName("icon")
    /**
     * The icon of the VosInfo.
     * */
    private int icon;

    public int getIcon() {
        return icon;
    }

    public int getAssociatedDrawable() {
        return VosInfo.getAssociatedDrawable(this);
    }

    public int getAssociatedColor()
    {
        return VosInfo.getAssociatedColor(this);
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(datetime);
        dest.writeString(team);
        dest.writeString(teamName);
        dest.writeString(note);
        dest.writeString(extra);
        dest.writeInt(hintNr);
        dest.writeInt(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VosInfo> CREATOR = new Creator<VosInfo>() {
        @Override
        public VosInfo createFromParcel(Parcel in) {
            return new VosInfo(in);
        }

        @Override
        public VosInfo[] newArray(int size) {
            return new VosInfo[size];
        }
    };

    /**
     * Deserializes a VosInfo from JSON.
     *
     * @param json The JSON where the VosInfo should be deserialized from.
     * @return The VosInfo deserialized from the JSON.
     */
    @Nullable
    public static VosInfo fromJson(String json) {
        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
            jsonReader.setLenient(true);
            return new Gson().fromJson(jsonReader, VosInfo.class);
        } catch (JsonParseException e)
        {
            Log.e("VosInfo", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Deserializes a VosInfo array from JSON.
     *
     * @param json The JSON where the array should be deserialized from.
     * @return The array of VosInfo deserialized from the JSON.
     */
    @Nullable
    public static VosInfo[] fromJsonArray(String json) {
        try {
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
            jsonReader.setLenient(true);
            return new Gson().fromJson(jsonReader, VosInfo[].class);
        } catch (JsonParseException e)
        {
            Log.e("VosInfo", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Gets the Drawable associated with the given VosInfo.
     *
     * @param info The VosInfo to get the associated icon from.
     * @return A int pointing to the associated drawable.
     * */
    public static int getAssociatedDrawable(VosInfo info)
    {
        switch (info.icon)
        {
            case SightingIcon.DEFAULT:
                switch (info.team)
                {
                    case "a":
                        return R.drawable.vos_a_2;
                    case "b":
                        return R.drawable.vos_b_2;
                    case "c":
                        return R.drawable.vos_c_2;
                    case "d":
                        return R.drawable.vos_d_2;
                    case "e":
                        return R.drawable.vos_e_2;
                    case "f":
                        return R.drawable.vos_f_2;
                    case "x":
                        return R.drawable.vos_x_2;
                }
                break;
            case SightingIcon.HUNT:
                switch (info.team)
                {
                    case "a":
                        return R.drawable.vos_a_4;
                    case "b":
                        return R.drawable.vos_b_4;
                    case "c":
                        return R.drawable.vos_c_4;
                    case "d":
                        return R.drawable.vos_d_4;
                    case "e":
                        return R.drawable.vos_e_4;
                    case "f":
                        return R.drawable.vos_f_4;
                    case "x":
                        return R.drawable.vos_x_4;
                }
                break;
            case SightingIcon.SPOT:
                switch (info.team)
                {
                    case "a":
                        return R.drawable.vos_a_3;
                    case "b":
                        return R.drawable.vos_b_3;
                    case "c":
                        return R.drawable.vos_c_3;
                    case "d":
                        return R.drawable.vos_d_3;
                    case "e":
                        return R.drawable.vos_e_3;
                    case "f":
                        return R.drawable.vos_f_3;
                    case "x":
                        return R.drawable.vos_x_3;
                }
                break;
            case SightingIcon.LAST_LOCATION:
                switch (info.team)
                {
                    case "a":
                        return R.drawable.vos_a_1;
                    case "b":
                        return R.drawable.vos_b_1;
                    case "c":
                        return R.drawable.vos_c_1;
                    case "d":
                        return R.drawable.vos_d_1;
                    case "e":
                        return R.drawable.vos_e_1;
                    case "f":
                        return R.drawable.vos_f_1;
                    case "x":
                        return R.drawable.vos_x_1;
                }
                break;
            default:
                switch (info.team)
                {
                    case "a":
                        return R.drawable.vos_a_2;
                    case "b":
                        return R.drawable.vos_b_2;
                    case "c":
                        return R.drawable.vos_c_2;
                    case "d":
                        return R.drawable.vos_d_2;
                    case "e":
                        return R.drawable.vos_e_2;
                    case "f":
                        return R.drawable.vos_f_2;
                    case "x":
                        return R.drawable.vos_x_2;
                }
                break;
        }
        return 0;
    }


    public static int getAssociatedColor(VosInfo info) {
        return getAssociatedColor(info.team);
    }

    public static int getAssociatedColor(String team) {
        switch (team)
        {
            case "a":
                return Color.argb(255, 255, 0, 0);
            case "b":
                return Color.argb(255, 0, 255, 0);
            case "c":
                return Color.argb(255, 0, 0, 255);
            case "d":
                return Color.argb(255, 0, 255, 255);
            case "e":
                return Color.argb(255, 255, 0, 255);
            case "f":
                return Color.argb(255, 255, 162, 0);
            case "x":
                return Color.argb(255, 0, 0, 0);
            default:
                return Color.WHITE;
        }
    }

}
