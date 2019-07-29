package nl.rsdt.japp.jotial.maps.misc

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 9-10-2016
 * Description...
 */

object VosUtils {

    fun calculateTimeDifference(t1: Date, t2: Date): Float {
        return (t1.time - t2.time).toFloat()
    }

    fun calculateTimeDifference(t1: Long, t2: Long): Long {
        return t1 - t2
    }

    fun calculateTimeDifferenceInHours(t1: Date, t2: Date): Float {
        return calculateTimeDifference(t1, t2) / 1000f / 60f / 60f
    }

    fun calculateTimeDifferenceInHoursFromNow(old: Date): Float {
        return calculateTimeDifference(Date(), old) / 1000f / 60f / 60f
    }

    fun calculateTimeDifferenceFromNow(old: Date): Float {
        return calculateTimeDifference(Date(), old)
    }

    fun calculateTimeDifferenceFromNow(old: Long): Float {
        return calculateTimeDifference(Date().time, old).toFloat()
    }

    fun calculateRadius(old: Long, speed: Float): Float {
        val diffMs = calculateTimeDifferenceFromNow(old)
        val diffS = diffMs / 1000
        return diffS * (speed / 3.6f)
    }

    fun calculateRadius(old: Date, speed: Float): Float {
        return calculateRadius(old.time, speed)
    }

    fun parseDate(value: String): Date? {
        var date: Date? = null
        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
            date = format.parse(value)
        } catch (e: ParseException) {
            Log.e("VosUtils", e.toString(), e)
        }

        return date
    }

    fun calculateRadius(old: String, speed: Float): Float {
        var date: Date? = null
        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
            date = format.parse(old)
        } catch (e: ParseException) {
            Log.e("VosUtils", e.toString(), e)
        }

        return date?.let { calculateRadius(it, speed) } ?: 500f
    }

}
