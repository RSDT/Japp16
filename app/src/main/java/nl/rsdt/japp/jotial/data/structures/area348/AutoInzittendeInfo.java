package nl.rsdt.japp.jotial.data.structures.area348;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mattijn on 30/09/17.
 */

public class AutoInzittendeInfo implements Parcelable{

    public int id;
    public String datetime;
    public String gebruikersNaam;
    public int gebruikersID;
    public String autoEigenaar;
    public String rol;
    /**
     * Initializes a new instance of BaseInfo from the parcel.
     *
     * @param in
     */
    protected AutoInzittendeInfo(Parcel in) {
        id = in.readInt();
        datetime = in.readString();
        gebruikersNaam = in.readString();
        gebruikersID = in.readInt();
        autoEigenaar = in.readString();
        rol = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int fags) {
        dest.writeInt(id);
        dest.writeString(datetime);
        dest.writeString(gebruikersNaam);
        dest.writeInt(gebruikersID);
        dest.writeString(autoEigenaar);
        dest.writeString(rol);
    }
    public static final Creator<AutoInzittendeInfo> CREATOR = new Creator<AutoInzittendeInfo>() {
        @Override
        public AutoInzittendeInfo createFromParcel(Parcel in) {
            return new AutoInzittendeInfo(in);
        }

        @Override
        public AutoInzittendeInfo[] newArray(int size) {
            return new AutoInzittendeInfo[size];
        }
    };
}
