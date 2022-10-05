package nl.rsdt.japp.jotial.maps.wrapper.google

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import org.acra.ktx.sendWithAcra

/**
 * Created by mattijn on 08/08/17.
 */

class GoogleMarker(private val googleMarker: com.google.android.gms.maps.model.Marker) : IMarker {
    override val identifier: MarkerIdentifier?
        get() {
            return if (title.isNotBlank()){
                try {
                    Gson().fromJson(title, MarkerIdentifier::class.java)?:null
                } catch (e: Exception) {
                    Log.e("GoogleMarker", e.toString())
                    e.sendWithAcra()
                    null
                }
            }
            else {
                null
            }
        }
    private var onClickListener: IMarker.OnClickListener? = null

    override var title: String
        get() = this.googleMarker.title
        set(title) {
            googleMarker.title = title
        }

    override var position: LatLng
        get() = this.googleMarker.position
        set(latLng) {
            this.googleMarker.position = latLng
        }

    override var isVisible: Boolean
        get() = this.googleMarker.isVisible
        set(visible) {
            googleMarker.isVisible = visible
        }

    override val id: String
        get() = googleMarker.id

    internal fun onClick(): Boolean {
        allOnClickLister?.OnClick(this)
        onClickListener?.OnClick(this)
        return false
    }

    override fun showInfoWindow() {
        googleMarker.showInfoWindow()
    }

    override fun remove() {
        this.googleMarker.remove()
    }

    override fun setOnClickListener(onClickListener: IMarker.OnClickListener?) {
        this.onClickListener = onClickListener
    }

    override fun setIcon(drawableHunt: Int) {
        this.setIcon(BitmapFactory.decodeResource(Japp.appResources, drawableHunt))
    }

    override fun setIcon(bitmap: Bitmap?) {
        this.googleMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }

    override fun setRotation(rotation: Float) {
        googleMarker.rotation = rotation
    }

    companion object {
        private var allOnClickLister: IJotiMap.OnMarkerClickListener? = null

        fun setAllOnClickLister(onClickListener: IJotiMap.OnMarkerClickListener?) {
            allOnClickLister = onClickListener
        }
    }
}
