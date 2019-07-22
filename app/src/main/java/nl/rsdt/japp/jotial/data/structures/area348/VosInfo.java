package nl.rsdt.japp.jotial.data.structures.area348;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
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

    public int getAssociatedColor() {
        return VosInfo.getAssociatedColor(this);
    }

    public int getAssociatedColor(int alpha) {
        return VosInfo.getAssociatedColor(this, alpha);
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
        MetaColorInfo.ColorNameInfo.DeelgebiedColor color = MetaColorInfo.ColorNameInfo.DeelgebiedColor.valueOf(JappPreferences.getColorName(info.team));
        switch (info.icon)
        {
            default:
            case SightingIcon.DEFAULT:
                switch (color)
                {
                    case Groen:
                        return R.drawable.vos_groen_2;
                    case Rood:
                        return R.drawable.vos_rood_2;
                    case Paars:
                        return R.drawable.vos_paars_2;
                    case Oranje:
                        return R.drawable.vos_oranje_2;
                    case Blauw:
                        return R.drawable.vos_blauw_2;
                    case Zwart:
                        return R.drawable.vos_zwart_2;
                    case Turquoise:
                        return R.drawable.vos_turquoise_2;
                    case Onbekend:
                        return R.drawable.vos_zwart_2;
                }
                break;
            case SightingIcon.HUNT:
                switch (color)
                {
                    case Groen:
                        return R.drawable.vos_groen_4;
                    case Rood:
                        return R.drawable.vos_rood_4;
                    case Paars:
                        return R.drawable.vos_paars_4;
                    case Oranje:
                        return R.drawable.vos_oranje_4;
                    case Blauw:
                        return R.drawable.vos_blauw_4;
                    case Zwart:
                        return R.drawable.vos_zwart_4;
                    case Turquoise:
                        return R.drawable.vos_turquoise_4;
                    case Onbekend:
                        return R.drawable.vos_zwart_4;
                }
                break;
            case SightingIcon.SPOT:
                switch (color)
                {
                    case Groen:
                        return R.drawable.vos_groen_3;
                    case Rood:
                        return R.drawable.vos_rood_3;
                    case Paars:
                        return R.drawable.vos_paars_3;
                    case Oranje:
                        return R.drawable.vos_oranje_3;
                    case Blauw:
                        return R.drawable.vos_blauw_3;
                    case Zwart:
                        return R.drawable.vos_zwart_3;
                    case Turquoise:
                        return R.drawable.vos_turquoise_3;
                    case Onbekend:
                        return R.drawable.vos_zwart_3;
                }
                break;
            case SightingIcon.LAST_LOCATION:
                switch (color)
                {
                    case Groen:
                        return R.drawable.vos_groen_1;
                    case Rood:
                        return R.drawable.vos_rood_1;
                    case Paars:
                        return R.drawable.vos_paars_1;
                    case Oranje:
                        return R.drawable.vos_oranje_1;
                    case Blauw:
                        return R.drawable.vos_blauw_1;
                    case Zwart:
                        return R.drawable.vos_zwart_1;
                    case Turquoise:
                        return R.drawable.vos_turquoise_1;
                    case Onbekend:
                        return R.drawable.vos_zwart_1;
                }
                break;
        }
        return R.drawable.vos_zwart_1;
    }


    public static int getAssociatedColor(VosInfo info) {
        return getAssociatedColor(info.team);
    }

    public static int getAssociatedColor(VosInfo info, int alpha) { return getAssociatedColor(info.team, alpha); }

    public static int getAssociatedColor(String team) {
        return getAssociatedColor(team, 255);
    }

    public static int getAssociatedColor(String team, int alpha) {
        MetaColorInfo.ColorNameInfo.DeelgebiedColor color = MetaColorInfo.ColorNameInfo.DeelgebiedColor.valueOf(JappPreferences.getColorName(team));
        switch (color)
        {
            case Rood:
                return Color.argb(alpha, 255, 0, 0);
            case Groen:
                return Color.argb(alpha, 0, 255, 0);
            case Blauw:
                return Color.argb(alpha, 0, 0, 255);
            case Turquoise:
                return Color.argb(alpha, 0, 255, 255);
            case Paars:
                return Color.argb(alpha, 255, 0, 255);
            case Oranje:
                return Color.argb(alpha, 255, 162, 0);
            case Zwart:
                return Color.argb(alpha, 0, 0, 0);
            case Onbekend:
            default:
                return Color.WHITE;
        }
    }


}
