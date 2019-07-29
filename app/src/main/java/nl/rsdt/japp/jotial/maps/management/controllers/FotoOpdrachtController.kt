package nl.rsdt.japp.jotial.maps.management.controllers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Pair
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo
import nl.rsdt.japp.jotial.data.structures.area348.FotoOpdrachtInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.management.MapItemUpdatable
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.management.StandardMapItemController
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.jotial.net.apis.FotoApi
import org.apache.commons.lang3.mutable.Mutable
import retrofit2.Call
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
class FotoOpdrachtController : StandardMapItemController<FotoOpdrachtInfo, FotoOpdrachtController.FotoOpdrachtTransducer.Result>() {

    override val id: String
        get() = CONTROLLER_ID

    override val storageId: String
        get() = STORAGE_ID

    override val bundleId: String
        get() = BUNDLE_ID

    override val transducer: FotoOpdrachtTransducer
        get() = FotoOpdrachtTransducer()

    override fun update(mode: String): Call<ArrayList<FotoOpdrachtInfo>>? {
        val api = Japp.getApi(FotoApi::class.java)
        when (mode) {
            MapItemUpdatable.MODE_ALL -> return api.getAll(JappPreferences.accountKey)
            MapItemUpdatable.MODE_LATEST -> return api.getAll(JappPreferences.accountKey)
        }
        return null
    }

    override fun searchFor(query: String): IMarker? {
        val results = ArrayList<BaseInfo>()
        var info: FotoOpdrachtInfo?
        for (i in items!!.indices) {
            info = items!![i]
            if (info != null) {

                var current: String?
                val items = arrayOf(info.info, info.extra)
                for (x in items.indices) {
                    current = items[x]
                    if (current?.toLowerCase(Locale.ROOT)?.startsWith(query)==true) results.add(info)
                }
            }
        }
        return null
    }

    override fun provide(): MutableList<String> {
        val results = ArrayList<String>()
        var info: FotoOpdrachtInfo?
        for (i in items!!.indices) {
            info = items!![i]
            if (info != null) {
                results.add(info.info?: "null")
                results.add(info.extra?: "null")
            }
        }
        return results
    }


    class FotoOpdrachtTransducer : AbstractTransducer<ArrayList<FotoOpdrachtInfo>, FotoOpdrachtTransducer.Result>() {

        override fun load(): ArrayList<FotoOpdrachtInfo>? {
            return AppData.getObject<ArrayList<FotoOpdrachtInfo>>(STORAGE_ID, object : TypeToken<ArrayList<FotoOpdrachtInfo>>() {

            }.type)
        }

        override fun transduceToBundle(bundle: Bundle) {
            load()?.let { bundle.putParcelable(BUNDLE_ID, generate(it)) }
        }

        override fun generate(items: ArrayList<FotoOpdrachtInfo>): Result {
            if (items == null || items.isEmpty()) return Result()

            val result = Result()
            result.bundleId = BUNDLE_ID
            result.addItems(items)

            if (isSaveEnabled) {
                AppData.saveObjectAsJson(items, STORAGE_ID)
            }


            var info: FotoOpdrachtInfo?

            /**
             * Loops through each FotoOpdrachtInfo.
             */
            for (i in items.indices) {
                info = items[i]
                if (info != null) {

                    val identifier = MarkerIdentifier.Builder()
                            .setType(MarkerIdentifier.TYPE_FOTO)
                            .add("info", info.info)
                            .add("extra", info.extra)
                            .add("icon", info.associatedDrawable.toString())
                            .create()

                    val mOptions = MarkerOptions()
                    mOptions.anchor(0.5f, 0.5f)
                    mOptions.position(info.latLng)
                    mOptions.title(Gson().toJson(identifier))
                    val bm: Bitmap
                    if (info.klaar == 1) {
                        bm = BitmapFactory.decodeResource(Japp.instance!!.resources, R.drawable.camera_20x20_klaar)
                    } else {
                        bm = BitmapFactory.decodeResource(Japp.instance!!.resources, R.drawable.camera_20x20)
                    }
                    result.add(Pair(mOptions, bm))
                }
            }
            return result
        }

        /**
         * @author Dingenis Sieger Sinke
         * @version 1.0
         * @since 31-7-2016
         * Description...
         */
        class Result : AbstractTransducer.StandardResult<FotoOpdrachtInfo> {

            constructor()

            /**
             * Reconstructs the result.
             *
             * @param in The parcel where the result was written to
             */
            protected constructor(`in`: Parcel) : super(`in`) {
                items = `in`.createTypedArrayList(FotoOpdrachtInfo.CREATOR)
            }

            override fun writeToParcel(dest: Parcel, flags: Int) {
                super.writeToParcel(dest, flags)
                dest.writeTypedList(items)
            }

            companion object CREATOR: Parcelable.Creator<Result> {

                    override fun createFromParcel(`in`: Parcel): Result {
                        return Result(`in`)
                    }

                    override fun newArray(size: Int): Array<Result?> {
                        return arrayOfNulls(size)
                    }

            }
        }

    }

    companion object {

        val CONTROLLER_ID = "FotoOpdrachtController"

        val STORAGE_ID = "STORAGE_FOTO"

        val BUNDLE_ID = "FOTO"

        val REQUEST_ID = "REQUEST_FOTO"
    }

}
