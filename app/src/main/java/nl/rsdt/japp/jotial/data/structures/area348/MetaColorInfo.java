package nl.rsdt.japp.jotial.data.structures.area348;

import android.os.Parcel;
import android.os.Parcelable;

public class MetaColorInfo extends BaseInfo implements Parcelable {
    /**
     * Initializes a new instance of BaseInfo from the parcel.
     *
     * @param in
     */
    public ColorNameInfo ColorName;
    public ColorHexInfo ColorCode;
    protected MetaColorInfo(Parcel in) {
        super(in);
    }
    public class ColorHexInfo{
        public String a;
        public String b;
        public String c;
        public String d;
        public String e;
        public String f;
        public String x;

    }
    public static class ColorNameInfo{
        public String a;
        public String b;
        public String c;
        public String d;
        public String e;
        public String f;
        public String x;
        public enum DeelgebiedColor{
            Groen,
            Rood,
            Paars,
            Oranje,
            Blauw,
            Zwart,
            Turquoise,
            Onbekend

        }
    }
}
