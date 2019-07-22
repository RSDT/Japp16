package nl.rsdt.japp.jotial.data.structures.area348;

import android.os.Parcel;
import android.os.Parcelable;

public class AutoEigenaarInfo implements Parcelable {
    public String autoEigenaar;
    protected AutoEigenaarInfo(Parcel in) {
        autoEigenaar = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int fags) {
        dest.writeString(autoEigenaar);
    }
    public static final Creator<AutoEigenaarInfo> CREATOR = new Creator<AutoEigenaarInfo>() {
        @Override
        public AutoEigenaarInfo createFromParcel(Parcel in) {
            return new AutoEigenaarInfo(in);
        }

        @Override
        public AutoEigenaarInfo[] newArray(int size) {
            return new AutoEigenaarInfo[size];
        }
    };
}
