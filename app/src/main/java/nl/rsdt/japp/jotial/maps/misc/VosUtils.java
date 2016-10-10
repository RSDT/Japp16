package nl.rsdt.japp.jotial.maps.misc;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 9-10-2016
 * Description...
 */

public class VosUtils {

    public static float calculateTimeDifference(Date t1, Date t2) {
        return t1.getTime() - t2.getTime();
    }

    public static long calculateTimeDifference(long t1, long t2) {
        return t1 - t2;
    }

    public static float calculateTimeDifferenceInHours(Date t1, Date t2) {
        return calculateTimeDifference(t1, t2) / 1000f / 60f / 60f;
    }

    public static float calculateTimeDifferenceInHoursFromNow(Date old) {
        return calculateTimeDifference(new Date(), old) / 1000f / 60f / 60f;
    }

    public static float calculateTimeDifferenceFromNow(Date old) {
        return calculateTimeDifference(new Date(), old);
    }

    public static float calculateTimeDifferenceFromNow(long old) {
        return calculateTimeDifference(new Date().getTime(), old);
    }

    public static float calculateRadius(long old, float speed) {
        float diffMs = calculateTimeDifferenceFromNow(old);
        float diffS = diffMs / 1000;
        return diffS * (speed / 3.6f );
    }

    public static float calculateRadius(Date old, float speed) {
        return calculateRadius(old.getTime(), speed);
    }

    public static Date parseDate(String value) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
            date = format.parse(value);
        } catch (ParseException e) {
            Log.e("VosUtils", e.toString(), e);
        }
        return date;
    }

    public static float calculateRadius(String old, float speed) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
            date = format.parse(old);
        } catch (ParseException e) {
            Log.e("VosUtils", e.toString(), e);
        }
        if(date != null) {
            return calculateRadius(date, speed);
        }
        return 500;
    }

}
