package nl.rsdt.japp.jotial.maps.wrapper

import android.graphics.Bitmap

import com.google.android.gms.maps.model.LatLng
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier

/**
 * Created by mattijn on 08/08/17.
 */

interface IMarker {

    var title: String

    var position: LatLng

    var isVisible: Boolean

    val id: String

    fun showInfoWindow()

    fun remove()

    fun setOnClickListener(onClickListener: IMarker.OnClickListener?)

    fun setIcon(drawableHunt: Int)

    fun setIcon(bitmap: Bitmap?)

    fun setRotation(rotation: Float)


    interface OnClickListener {
        fun OnClick(m: IMarker): Boolean
    }

    val identifier: MarkerIdentifier?
}
