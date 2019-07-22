package nl.rsdt.japp.jotial.data.structures.area348;

import android.os.Parcel;
import android.os.Parcelable;

public class DeletedInfo implements Parcelable {
    public boolean verwijderd;
    protected DeletedInfo(Parcel in) {
        verwijderd = true;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int fags) {

    }
    public static final Creator<DeletedInfo> CREATOR = new Creator<DeletedInfo>() {
        @Override
        public DeletedInfo createFromParcel(Parcel in) {
            return new DeletedInfo(in);
        }

        @Override
        public DeletedInfo[] newArray(int size) {
            return new DeletedInfo[size];
        }
    };
}
